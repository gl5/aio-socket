package io.github.mxd888.demo.client;

import io.github.mxd888.demo.common.DemoPacket;
import io.github.mxd888.socket.buffer.BufferPagePool;
import io.github.mxd888.socket.core.Aio;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.core.ClientBootstrap;
import io.github.mxd888.socket.plugins.ReconnectPlugin;

import java.io.IOException;
import java.io.PrintStream;


/**
 * -----5seconds ----
 * inflow:		556.9914922714233(MB)
 * outflow:	560.5402374267578(MB)
 * process fail:	0
 * process count:	29084023
 * process total:	148858031
 * read count:	552	write count:	4732
 * connect count:	0
 * disconnect count:	0
 * online count:	10
 * connected total:	10
 * Requests/sec:	5816804.6
 * Transfer/sec:	111.39829845428467(MB)
 */
public class Client {

    public static void main(String[] args) {

//        byte b = (byte) 0x7f;
//        System.out.println(b);

        PrintStream ps = new PrintStream(System.out){
            @Override
            public void println(String x) {
                if(filterLog(x)){
                    return;
                }
                super.println(x);
            }
            @Override
            public void print(String s) {
                if(filterLog(s)){
                    return;
                }
                super.print(s);
            }
        };
        System.setOut(ps);

        DemoPacket demoPacket = new DemoPacket("hello aio-socket");
        // 5000
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            new Thread(() -> {
                // 81.70.149.16 127.0.0.1
                ClientBootstrap clientBootstrap = new ClientBootstrap((args != null && args.length != 0) ? args[0] : "127.0.0.1", (args != null && args.length != 0) ? Integer.parseInt(args[1]) : 8888, new ClientHandler());
                clientBootstrap.getConfig().setEnablePlugins(true);
                clientBootstrap.getConfig().setHeartPacket(new DemoPacket("heart message"));
                clientBootstrap.getConfig().setReadBufferSize(1024 * 1024);
                clientBootstrap.getConfig().setWriteBufferSize(1024 * 1024);
                clientBootstrap.getConfig().setWriteBufferCapacity(16);
                clientBootstrap.getConfig().getPlugins().addPlugin(new ReconnectPlugin(clientBootstrap));
                clientBootstrap.getConfig().setBufferFactory(() -> new BufferPagePool(5 * 1024 * 1024, 9, false));
                if (((args != null && args.length != 0) ? Integer.parseInt(args[2]) : 0) == 1) {
                    // 启用内核增强
                    clientBootstrap.getConfig().setEnhanceCore(true);
                    System.out.println("启动内核增强");
                }
                try {
                    ChannelContext start = clientBootstrap.start();
                    long num = 0;
                    long startnum = System.currentTimeMillis();
                    while (num++ < Integer.MAX_VALUE) {
                        if (start == null) {
                            System.out.println("连接失败了.....");
                        }else {
//                            demoPacket.setData("奥德赛阿斯达大叔控阿斯达阿斯达asasd水电费是个是个个数人所能四收到广东省开发还是个但是不分开就是的个覅安抚");
                            Aio.send(start, demoPacket);
                        }
                    }
                    System.out.println("安全消息结束" + (System.currentTimeMillis() - startnum));
                    Thread.sleep(10000);
                    clientBootstrap.shutdown();
                } catch (IOException | InterruptedException e) {
                    System.out.println(finalI);
                    e.printStackTrace();
                }

            }).start();
        }
    }

    private static boolean filterLog(String x){
        return x.contains("aio-socket version: 2.10.1.v20211002-RELEASE;");
    }

}
