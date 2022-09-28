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
package io.github.mxd888.socket.utils.pool.thread;

import io.github.mxd888.socket.utils.queue.FullWaitQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

public abstract class AbstractQueueRunnable<T> extends AbstractSynRunnable {

    private static final Logger log = LoggerFactory.getLogger(AbstractQueueRunnable.class);

    /**
     * Instantiates a new abstract syn runnable.
     *
     * @param executor 并发安全线程池
     */
    protected AbstractQueueRunnable(Executor executor) {
        super(executor);
    }

    /**
     * 添加消息
     *
     * @return 添加状态
     */
    public boolean addMsg(T t) {
        if (this.isCanceled()) {
            log.error("任务已经取消");
            return false;
        }
        return getMsgQueue().offer(t);
    }

    /**
     * 清空处理的队列消息
     */
    public void clearMsgQueue() {
        if (getMsgQueue() != null) {
            getMsgQueue().clear();
        }
    }

    @Override
    public boolean isNeededExecute() {
        return  (getMsgQueue() != null && !getMsgQueue().isEmpty()) && !this.isCanceled();
    }

    /**
     * 获取消息队列
     * @return 消息队列
     */
    public abstract FullWaitQueue<T> getMsgQueue();
}
