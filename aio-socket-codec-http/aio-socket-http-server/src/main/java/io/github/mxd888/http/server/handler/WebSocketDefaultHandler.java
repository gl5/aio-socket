package io.github.mxd888.http.server.handler;

import io.github.mxd888.http.common.logging.Logger;
import io.github.mxd888.http.common.logging.LoggerFactory;
import io.github.mxd888.http.server.WebSocketHandler;
import io.github.mxd888.http.server.WebSocketRequest;
import io.github.mxd888.http.server.WebSocketResponse;
import io.github.mxd888.http.server.impl.Request;
import io.github.mxd888.http.server.impl.WebSocketRequestImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class WebSocketDefaultHandler extends WebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketDefaultHandler.class);

    @Override
    public void onHeaderComplete(Request request) throws IOException {
        super.onHeaderComplete(request);
        WebSocketRequestImpl webSocketRequest = request.newWebsocketRequest();
        onHandShake(webSocketRequest, webSocketRequest.getResponse());
    }

    @Override
    public final void handle(WebSocketRequest request, WebSocketResponse response) throws IOException {
        try {
            switch (request.getFrameOpcode()) {
                case WebSocketRequestImpl.OPCODE_TEXT:
                    handleTextMessage(request, response, new String(request.getPayload(), StandardCharsets.UTF_8));
                    break;
                case WebSocketRequestImpl.OPCODE_BINARY:
                    handleBinaryMessage(request, response, request.getPayload());
                    break;
                case WebSocketRequestImpl.OPCODE_CLOSE:
                    try {
                        onClose(request, response);
                    } finally {
                        response.close();
                    }
                    break;
                case WebSocketRequestImpl.OPCODE_PING:
//                            LOGGER.warn("unSupport ping now");
                    break;
                case WebSocketRequestImpl.OPCODE_PONG:
//                            LOGGER.warn("unSupport pong now");
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (Throwable throwable) {
            onError(request,throwable);
            throw throwable;
        }
    }

    /**
     * ????????????
     *
     * @param request
     * @param response
     */
    public void onHandShake(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("handShake success");
    }

    /**
     * ????????????
     *
     * @param request
     * @param response
     */
    public void onClose(WebSocketRequest request, WebSocketResponse response) {
        LOGGER.warn("close connection");
    }

    /**
     * ???????????????????????????
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
        System.out.println(data);
    }

    /**
     * ???????????????????????????
     *
     * @param request
     * @param response
     * @param data
     */
    public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
        System.out.println(data);
    }

    /**
     * ????????????
     *
     * @param request
     * @param throwable
     */
    public void onError(WebSocketRequest request,Throwable throwable) {
        throwable.printStackTrace();
    }
}
