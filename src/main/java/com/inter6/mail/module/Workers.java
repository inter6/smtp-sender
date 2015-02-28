package com.inter6.mail.module;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inter6.mail.job.thread.ThreadSupportJob;

/**
 * Worker 패턴의 ThreadPool 구현체<br>
 * <ul>
 * 	<li>기본 worker Thread 개수 : 1 ~ core*2</li>
 * 	<li>job 싸이클간 대기 시간 3초.</li>
 * 	<li>에러 발생시 대기 시간 10초.</li>
 * </ul>
 */
public class Workers {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final static int CPU_CORE_CNT = Runtime.getRuntime().availableProcessors();
	public final static int MAX_POOL_SIZE_DEFAULT = CPU_CORE_CNT * 2;

	private ThreadPoolExecutor workerPoolExecutor = null;
	private final RejectedExecutionHandler rejectedExecutionHandler = new BlockPolicy();
	private final AtomicInteger currentPoolNumber = new AtomicInteger(0);

	private String workersName = null;

	private int corePoolSize = CPU_CORE_CNT / 4;
	private int maximumPoolSize = MAX_POOL_SIZE_DEFAULT;

	private BlockingQueue<Runnable> workQueue = null;

	private final AtomicInteger currentExecuteCnt = new AtomicInteger();

	private int terminateTimeout = 3;
	private int keepAliveTime = 1;
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	public void initailize(final String workersName) {
		if (this.workerPoolExecutor != null) {
			throw new IllegalStateException("worker pool already initialized !");
		}

		this.workersName = workersName;
		this.workQueue = new LinkedBlockingQueue<Runnable>(Workers.MAX_POOL_SIZE_DEFAULT);

		this.workerPoolExecutor = this.createThreadPoolExecutor(workersName);
	}

	private ThreadPoolExecutor createThreadPoolExecutor(final String workersName) {
		ThreadFactory threadFactory = new WorkersFactory(workersName, this.currentPoolNumber.incrementAndGet());
		this.currentExecuteCnt.set(0);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(this.corePoolSize, this.maximumPoolSize, this.keepAliveTime, this.timeUnit, this.workQueue, threadFactory, this.rejectedExecutionHandler);
		this.log.info("new thread pool-" + this.currentPoolNumber.get() + " created!");
		return threadPoolExecutor;
	}

	public void execute(ThreadSupportJob workerJob) {
		this.workerPoolExecutor.execute(workerJob);
	}

	public void setPoolSize(int corePoolSize, int maximumPoolSize) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
	}

	public void setKeepAliveTime(int keepAliveTime, TimeUnit timeUnit) {
		this.keepAliveTime = keepAliveTime;
		this.timeUnit = timeUnit;
	}

	public void setTerminateTimeoutSeconds(int terminateTimeout) {
		this.terminateTimeout = terminateTimeout;
	}

	public void terminate() throws InterruptedException {
		if (this.workerPoolExecutor == null) {
			return;
		}
		this.log.info(this.getCurrentMonitoringInfo());
		this.log.info(this.workersName + " terminate start[max:" + this.terminateTimeout + "sec]...");
		this.workerPoolExecutor.shutdown();
		this.workerPoolExecutor.awaitTermination(this.terminateTimeout, TimeUnit.SECONDS);

		this.log.info(this.getCurrentMonitoringInfo());
		this.log.info(this.workersName + " terminate end");

	}

	public void stop() {
		if (this.workerPoolExecutor == null) {
			return;
		}
		this.log.info(this.workersName + " start the stop...");
		this.workQueue.clear();

		int i = 0;
		do {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// do nothing
			}
			this.log.info("waiting for " + ++i + " seconds to stop the walker..");
		} while (this.isRun());

		this.log.info(this.getCurrentMonitoringInfo());
		this.log.info(this.workersName + " stopped [" + i + " sec elapsed]");
	}

	public boolean isRun() {
		if (this.workerPoolExecutor == null) {
			return false;
		}
		if (this.workQueue.isEmpty()) {
			return this.workerPoolExecutor.getActiveCount() > 0;
		} else {
			return true;
		}
	}

	public String getCurrentMonitoringInfo() {
		if (this.workerPoolExecutor == null) {
			return "this workers did not yet initialized !";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("\nWorkers name:").append(this.workersName);
		sb.append("\nComplete task count:").append(this.workerPoolExecutor.getCompletedTaskCount());
		sb.append("\nCore worker count:").append(this.workerPoolExecutor.getCorePoolSize());
		sb.append("\nActive worker count:").append(this.workerPoolExecutor.getActiveCount());
		sb.append("\nMax worker count:").append(this.workerPoolExecutor.getMaximumPoolSize());
		sb.append("\nWorker keep alive time:").append(this.workerPoolExecutor.getKeepAliveTime(TimeUnit.SECONDS)).append("sec");
		return sb.toString();
	}

	public int getIntervalBySec() {
		return 60;
	}

	/**
	 * 큐가 꽉차면 Worker에게 job을 인계한 Thread가 queue에 빈 공간이 생길때 까지 block된다.<br>
	 * 일반적인 Worker패턴에서는 Master Thread에서 worker의 일을 수행하지 않는다.<br>
	 * java api에 존재하지 않아서 구현 - java 에서는 생산자/소비자 패턴만 지원
	 *
	 * @author ysko
	 *
	 */
	private static class BlockPolicy implements RejectedExecutionHandler {
		private final Logger log = LoggerFactory.getLogger(this.getClass());

		@Override
		public void rejectedExecution(Runnable job, ThreadPoolExecutor executor) {
			if (!executor.isShutdown()) {
				try {
					executor.getQueue().put(job);
				} catch (InterruptedException e) {
					this.log.error("block policy", e);
				}
			}
		}
	}

	/**
	 * woker Thread생성 지원을 위한 Factory<br>
	 * <ul>
	 * 	<li>daemon thread</li>
	 * 	<li>같은 thread group</li>
	 * </ul>
	 *
	 * @author ysko
	 *
	 */
	private static class WorkersFactory implements ThreadFactory {
		private final Logger log = LoggerFactory.getLogger(this.getClass());

		private final ThreadGroup threadGroup;

		private final AtomicInteger threadNumber = new AtomicInteger(1);

		private final String namePrefix;

		public WorkersFactory(String workerName, int poolNumber) {
			ThreadGroup parentThreadGroup = Thread.currentThread().getThreadGroup();
			this.threadGroup = new ThreadGroup(parentThreadGroup, workerName);
			this.namePrefix = workerName + "Pool-" + poolNumber + "-Worker-";
		}

		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(this.threadGroup, runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
			this.log.info(thread.getName() + " created !");
			// TODO deamon 확인
			if (!thread.isDaemon()) {
				thread.setDaemon(true);
			}
			// TODO 우선순위 설정 구현 여부를 판단해서 필요하면 기능 추가
			return thread;
		}
	}
}