package io.github.mxd888.socket.task;

import io.github.mxd888.socket.Monitor;
import io.github.mxd888.socket.StateMachineEnum;
import io.github.mxd888.socket.core.AioConfig;
import io.github.mxd888.socket.core.ChannelContext;
import io.github.mxd888.socket.utils.pool.thread.AbstractQueueRunnable;
import io.github.mxd888.socket.utils.queue.AioFullNotifyQueue;
import io.github.mxd888.socket.utils.queue.AioQueue;

import java.util.concurrent.Executor;

/**
 * 消息解码逻辑执行器
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
public class DecodeTask extends AbstractQueueRunnable<Integer> {

    private final ChannelContext channelContext;

    private final AioConfig aioConfig;

    private AioQueue<Integer> msgQueue = null;

    public DecodeTask(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        this.aioConfig = channelContext.getAioConfig();
        getTaskQueue();
    }

    public DecodeTask(ChannelContext channelContext, Executor executor, int maxExecuteNum) {
        super(executor, maxExecuteNum);
        this.channelContext = channelContext;
        this.aioConfig = channelContext.getAioConfig();
        getTaskQueue();
    }

    @Override
    public AioQueue<Integer> getTaskQueue() {
        if (msgQueue == null) {
            synchronized (this) {
                if (msgQueue == null) {
                    msgQueue = new AioFullNotifyQueue<>(aioConfig.getMaxWaitNum());
                }
            }
        }
        return msgQueue;
    }

    @Override
    public void runTask() {
        if (msgQueue.isEmpty()) {
            return;
        }

        Integer integer;
        while ((integer = msgQueue.poll()) != null) {
            decode(integer);
        }
    }

    private void decode(Integer result) {
        try {
            // 接收到的消息进行预处理
            Monitor monitor = channelContext.getAioConfig().getMonitor();
            if (monitor != null) {
                monitor.afterRead(channelContext, result);
            }
            //触发读回调
            channelContext.signalRead(result == -1);
        }catch (Exception exc) {
            channelContext.getAioConfig().getHandler().stateEvent(channelContext, StateMachineEnum.INPUT_EXCEPTION, exc);
            channelContext.close(false);
        }

    }
}
