package io.github.mxd888.socket.plugins;

import java.io.IOException;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于设置Socket Option的插件
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class SocketOptionPlugin extends AbstractPlugin {

    private final Map<SocketOption<Object>, Object> optionMap = new HashMap<>();

    @Override
    public final AsynchronousSocketChannel shouldAccept(AsynchronousSocketChannel channel) {
        setOption(channel);
        return super.shouldAccept(channel);
    }

    /**
     * 往socket channel中设置option值。
     * 默认将通过{@link #setOption(SocketOption, Object)}指定的配置值绑定到每一个Socket中。
     * 如果有个性化的需求,可以重新实现本方法。
     *
     * @param channel .
     */
    public void setOption(AsynchronousSocketChannel channel) {
        try {
            if (!optionMap.containsKey(StandardSocketOptions.TCP_NODELAY)) {
                channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
            }
            for (Map.Entry<SocketOption<Object>, Object> entry : optionMap.entrySet()) {
                channel.setOption(entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置Socket的TCP参数配置。
     * <p>
     * AIO客户端的有效可选范围为：<br/>
     * 1. StandardSocketOptions.SO_SNDBUF <br/>
     * 2. StandardSocketOptions.SO_RCVBUF<br/>
     * 3. StandardSocketOptions.SO_KEEPALIVE<br/>
     * 4. StandardSocketOptions.SO_REUSEADDR<br/>
     * 5. StandardSocketOptions.TCP_NODELAY<br/>
     * </p>
     *
     * @param socketOption 配置项
     * @param value        配置值
     * @return .
     */
    public final <V> SocketOptionPlugin setOption(SocketOption<V> socketOption, V value) {
        put0(socketOption, value);
        return this;
    }

    public final <V> V getOption(SocketOption<V> socketOption) {
        Object value = optionMap.get(socketOption);
        return value == null ? null : (V) value;
    }

    private void put0(SocketOption socketOption, Object value) {
        optionMap.put(socketOption, value);
    }
}