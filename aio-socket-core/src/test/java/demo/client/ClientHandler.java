package demo.client;


import demo.DemoPacket;
import demo.Handler;
import io.github.mxd888.socket.Packet;
import io.github.mxd888.socket.core.ChannelContext;


public class ClientHandler extends Handler {

    private long count = 0L;

    @Override
    public void handle(ChannelContext channelContext, Packet packet) {
        DemoPacket packet1 = (DemoPacket) packet;
        if (!packet1.getData().equals("hello aio-socket")) {
            System.out.println("不一致，出错啦:" + packet1.getData());
        }else {
            count++;
            if (count % 1000000 ==0) {
                System.out.println("已收到" + (count / 1000000) + "百万条消息");
            }
        }
    }
}
