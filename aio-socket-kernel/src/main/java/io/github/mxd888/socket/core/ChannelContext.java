/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.mxd888.socket.core;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.utils.pool.memory.MemoryBlock;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;
import io.github.mxd888.socket.utils.queue.AioFullWaitQueue;
import io.github.mxd888.socket.utils.queue.AioQueue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 抽象通道上下文
 */
public abstract class ChannelContext {

    /**
     * 用户绑定ID
     */
    private String id;

    /**
     * 附件对象
     */
    private Object attachment;

    /**
     * 输出流，用于往输出buffer里面输入数据的对象
     */
    protected WriteBuffer byteBuf;

    /**
     * 当前会话所属协议
     */
    private ProtocolEnum protocol;

    /**
     * 存放当前ChannelContext未解码的虚拟buffer
     */
    private AioQueue<MemoryUnit> oldByteBufferQueue;

    /**
     * 自定义属性Map
     */
    private final Map<String, Object> attr = new HashMap<>();

    /**
     * 当前会话状态
     */
    protected byte status = CHANNEL_STATUS_ENABLED;

    /**
     * ChannelContext状态:已关闭
     */
    protected static final byte CHANNEL_STATUS_CLOSED = 1;

    /**
     * ChannelContext状态:关闭中
     */
    protected static final byte CHANNEL_STATUS_CLOSING = 2;

    /**
     * ChannelContext状态:正常
     */
    protected static final byte CHANNEL_STATUS_ENABLED = 3;

    /**
     * 读取当前ChannelContext未解码的虚拟buffer
     *
     * @return aio-socket自制满等待队列
     */
    public AioQueue<MemoryUnit> getOldByteBuffer() {
        if (oldByteBufferQueue != null) {
            return oldByteBufferQueue;
        }
        synchronized (ChannelContext.class) {
            if (oldByteBufferQueue != null) {
                return oldByteBufferQueue;
            }
            oldByteBufferQueue = new AioFullWaitQueue<>(10);
        }
        return oldByteBufferQueue;
    }

    /**
     * 设置通道上下文的输出流
     *
     * @param memoryBlock 输出流所在内存页
     * @param consumer   消费函数
     * @param chunkSize  输出流大小（单位字节）
     * @param capacity   待输出数组的大小
     */
    protected void setWriteBuffer(MemoryBlock memoryBlock, Consumer<WriteBuffer> consumer, int chunkSize, int capacity) {
        if (byteBuf == null) {
            byteBuf = new WriteBuffer(memoryBlock, consumer, chunkSize, capacity);
        }
    }

    /**
     * 获取一个虚拟buffer用于存放数据
     * @param len 长度
     * @return 内存池虚拟buffer
     */
    public MemoryUnit getVirtualBuffer(int len) {
        return byteBuf.newVirtualBuffer(len);
    }

    /**
     * 获取通道输出流
     *
     * @return IO输出流
     */
    public WriteBuffer getWriteBuffer() {
        return byteBuf;
    }

    /**
     * 通道数据输出出
     */
    protected void flush(){
        byteBuf.flush();
    }

    /**
     * 获取读buffer，不要轻易用
     *
     * @return 读虚拟buffer
     */
    public abstract MemoryUnit getReadBuffer();

    /**
     * 关闭连接
     */
    public void close() {
        close(false);
    }

    /**
     * 是否立即关闭会话
     *
     * @param immediate true:立即关闭,false:响应消息发送完后关闭
     */
    public abstract void close(boolean immediate);

    /**
     * 获取通道ID
     *
     * @return 通道上下文唯一ID
     */
    public String getId() {
        return id;
    }

    /**
     * 通道上下文绑定唯一ID
     *
     * @param id ID内容
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取通道所属协议
     *
     * @return 协议枚举
     */
    public ProtocolEnum getProtocol() {
        return protocol;
    }

    /**
     * 设置通道所属协议
     *
     * @param protocol 协议枚举
     */
    public void setProtocol(ProtocolEnum protocol) {
        this.protocol = protocol;
    }

    /**
     * 读取通道
     * @param flip 是否flip
     */
    public abstract void signalRead(boolean flip);

    /**
     * 获取当前会话的本地连接地址
     *
     * @return 本地地址
     * @throws IOException IO异常
     * @see AsynchronousSocketChannel#getLocalAddress()
     */
    public abstract InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 获取当前会话的远程连接地址
     *
     * @return 远程地址
     * @throws IOException IO异常
     * @see AsynchronousSocketChannel#getRemoteAddress()
     */
    public abstract InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 当前会话是否已失效
     *
     * @return 是否失效
     */
    public boolean isInvalid() {
        return status != CHANNEL_STATUS_ENABLED;
    }

    /**
     * 获取附件对象
     *
     * @return 附件
     */
    public Object getAttachment() {
        return attachment;
    }

    /**
     * 存放附件，支持任意类型
     *
     * @param attachment 附件对象
     */
    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    /**
     * 属性获取
     * @param s key
     * @param t 类型
     * @param <T> 泛型
     * @return 值
     */
    @SuppressWarnings("all")
    public <T> T getAttr(String s, Class<T> t) {
        return (T) attr.get(s);
    }

    /**
     * 属性设置
     *
     * @param s key
     * @param o value
     */
    public void attr(String s, Object o) {
        attr.put(s, o);
    }

    /**
     * 获取配置文件
     *
     * @return 配置信息
     */
    public abstract AioConfig getAioConfig();

    /**
     * 发送器
     */
    protected abstract void sendPacket(Packet packet, boolean isBlock);
}
