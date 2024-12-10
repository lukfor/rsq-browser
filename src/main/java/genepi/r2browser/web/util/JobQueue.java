package genepi.r2browser.web.util;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import genepi.r2browser.model.Query;
import genepi.r2browser.model.Report;

public class JobQueue implements Runnable {

	private List<Runnable> queue = new Vector<Runnable>();

	private ThreadPoolExecutor scheduler;

	private static final Log log = LogFactory.getLog(JobQueue.class);

	public JobQueue(int threads) {
		scheduler = new ThreadPoolExecutor(threads, threads, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public void submit(Query job) {

		synchronized (queue) {

			Future l = scheduler.submit(job);
			queue.add(job);
			log.info("Submit query " + job.getId() + "...");

		}

	}

	public void submit(Report job) {

		synchronized (queue) {

			Future l = scheduler.submit(job);
			queue.add(job);
			log.info("Submit report " + job.getId() + "...");

		}

	}


	@Override
	public void run() {

		while (true) {
			try {

				Thread.sleep(5000);

			} catch (Exception e) {

				log.warn("Concurrency Exception!! ");
				e.printStackTrace();

			}

		}
	}

	public int getActiveCount() {
		return scheduler.getActiveCount();
	}

}