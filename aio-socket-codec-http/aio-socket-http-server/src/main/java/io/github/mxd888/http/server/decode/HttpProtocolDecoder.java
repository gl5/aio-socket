package io.github.mxd888.http.server.decode;

import io.github.mxd888.http.common.utils.ByteTree;
import io.github.mxd888.http.common.utils.StringUtils;
import io.github.mxd888.http.server.HttpServerConfiguration;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.TCPChannelContext;

import java.nio.ByteBuffer;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
class HttpProtocolDecoder extends AbstractDecoder {

    private final HttpHeaderDecoder decoder = new HttpHeaderDecoder(getConfiguration());

    private final LfDecoder lfDecoder = new LfDecoder(decoder, getConfiguration());

    public HttpProtocolDecoder(HttpServerConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Decoder decode(ByteBuffer byteBuffer, ChannelContext channelContext, Request request) {
        ByteTree<?> protocol = StringUtils.scanByteTree(byteBuffer, CR_END_MATCHER, getConfiguration().getByteCache());
        if (protocol != null) {
            request.setProtocol(protocol.getStringValue());
            return lfDecoder.decode(byteBuffer, channelContext, request);
        } else {
            return this;
        }

    }
}
