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
package io.github.mxd888.socket.task;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.pool.thread.AbstractQueueRunnable;
import io.github.mxd888.socket.utils.queue.AioFullWaitQueue;
import io.github.mxd888.socket.utils.queue.FullWaitQueue;

import java.util.concurrent.Executor;

/**
 * 处理逻辑执行器
 */
public class HandlerRunnable extends AbstractQueueRunnable<Packet> {

    private final ChannelContext channelContext;
    private final AioConfig tioConfig;
    private FullWaitQueue<Packet> msgQueue = null;

    public HandlerRunnable(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        tioConfig = channelContext.getAioConfig();
        getMsgQueue();
    }

    /**
     * 处理packet
     * @param packet .
     *
     * @author tanyaowu
     */
    public void handler(Packet packet) {
        try {
            // 处理消息
            Packet handle = tioConfig.getHandler().handle(channelContext, packet);
            if (handle != null) {
                Aio.send(channelContext, handle);
            }
        } catch (Exception e) {
            tioConfig.getHandler().stateEvent(channelContext, StateMachineEnum.PROCESS_EXCEPTION, e);
        }

    }

    /**
//     * @see org.tio.core.SynRunnable.intf.ISynRunnable#runTask()
     *
     * @author tanyaowu
     * 2016年12月5日 下午3:02:49
     *
     */
    @Override
    public void runTask() {
        Packet packet;
        while ((packet = msgQueue.poll()) != null) {
            handler(packet);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + channelContext.toString();
    }

    @Override
    public String logstr() {
        return toString();
    }

    @Override
    public FullWaitQueue<Packet> getMsgQueue() {
        if (msgQueue == null) {
            synchronized (this) {
                if (msgQueue == null) {
                    msgQueue = new AioFullWaitQueue<>(tioConfig.getMaxWaitNum(), true);
                }
            }
        }
        return msgQueue;
    }

}
