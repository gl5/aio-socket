package http;

import io.github.mxd888.http.server.*;
import io.github.mxd888.http.server.handler.HttpRouteHandler;
import io.github.mxd888.http.server.handler.WebSocketDefaultHandler;
import io.github.mxd888.http.server.handler.WebSocketRouteHandler;

import java.io.IOException;

public class HTTPServer {

    public static void main(String[] args) {
        //1. 实例化路由Handle
        HttpRouteHandler routeHandle = new HttpRouteHandler();

        //2. 指定路由规则以及请求的处理实现
        routeHandle.route("/", new HttpServerHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response) throws IOException {
                response.write("smart-http".getBytes());
            }
        }).route("/test1", new HttpServerHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response) throws IOException {
                response.write(("test1").getBytes());
            }
        }).route("/test2", new HttpServerHandler() {
            @Override
            public void handle(HttpRequest request, HttpResponse response) throws IOException {
                response.write(("test2").getBytes());
            }
        });

        // 3. 启动服务
        HttpBootstrap bootstrap = new HttpBootstrap();
        bootstrap.httpHandler(routeHandle);

        WebSocketRouteHandler wsRouteHandle = new WebSocketRouteHandler();
        wsRouteHandle.route("/ws", new WebSocketDefaultHandler() {
            @Override
            public void onHandShake(WebSocketRequest request, WebSocketResponse webSocketResponse) {
                System.out.println("收到握手消息");
            }

            @Override
            public void handleTextMessage(WebSocketRequest request, WebSocketResponse response, String data) {
                System.out.println("收到请求消息:" + data);
                response.sendTextMessage("服务端收到响应:" + data);
            }

            @Override
            public void onClose(WebSocketRequest request, WebSocketResponse response) {
                super.onClose(request, response);
            }

            @Override
            public void handleBinaryMessage(WebSocketRequest request, WebSocketResponse response, byte[] data) {
                response.sendBinaryMessage(data);
            }
        });
        bootstrap.webSocketHandler(wsRouteHandle);

        bootstrap.start();
    }
}
