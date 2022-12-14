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
package io.github.mxd888.socket.codec.string;

import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.intf.AioHandler;
import io.github.mxd888.socket.intf.IProtocol;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.WriteBuffer;
import io.github.mxd888.socket.exception.AioDecoderException;
import io.github.mxd888.socket.intf.Handler;
import io.github.mxd888.socket.utils.AIOUtil;
import io.github.mxd888.socket.ProtocolEnum;
import io.github.mxd888.socket.utils.pool.memory.MemoryUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public abstract class StringHandler extends AioHandler implements Handler, IProtocol {

    private int maxLength;

    private Charset charsets;

    public StringHandler() {
    }

    public StringHandler(int maxLength) {
        this.maxLength = maxLength;
    }

    public StringHandler(int maxLength, Charset charsets) {
        this(maxLength);
        this.charsets = charsets;
    }

    @Override
    public Packet handle(ChannelContext channelContext, Packet packet) {
        if (packet instanceof StringPacket) {
            return handle(channelContext, (StringPacket) packet);
        }
        return null;
    }

    @Override
    public Packet decode(MemoryUnit memoryUnit, ChannelContext channelContext) throws AioDecoderException {
        ByteBuffer buffer = memoryUnit.buffer();
        int remaining = buffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        buffer.mark();
        int length = buffer.getInt();
        if (maxLength > 0 && length > maxLength) {
            buffer.reset();
            return null;
        }
        byte[] b = AIOUtil.getBytesFromByteBuffer(length, memoryUnit, channelContext);
        if (b == null) {
            buffer.reset();
            return null;
        }
        // ?????????UTF_8???????????????8%
        return charsets != null ? new StringPacket(new String(b, charsets)) : new StringPacket(new String(b));
    }

    @Override
    public void encode(Packet packet, ChannelContext channelContext) {
        WriteBuffer writeBuffer = channelContext.getWriteBuffer();
        try {
            StringPacket packet1 = (StringPacket) packet;
            writeBuffer.writeInt(packet1.getData().getBytes().length);
            writeBuffer.write(packet1.getData().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ProtocolEnum name() {
        return ProtocolEnum.STRING;
    }

    public abstract Packet handle(ChannelContext channelContext, StringPacket packet);
}
