package com.inter6.mail.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.stereotype.Component;

import com.inter6.mail.job.Job;

@Component
public class JobStatistics {

	private final MultiKeyMap<String, AtomicInteger> countMap = new MultiKeyMap<String, AtomicInteger>();

	public void addCount(Job job, String category) {
		this.addCount(job.getClass().getSimpleName(), category);
	}

	public void addCount(String jobName, String category) {
		AtomicInteger count = this.countMap.get(jobName, category);
		if (count == null) {
			count = new AtomicInteger();
			this.countMap.put(jobName, category, count);
		}
		count.incrementAndGet();
	}

	public int getCount(String jobName, String category) {
		AtomicInteger count = this.countMap.get(jobName, category);
		if (count == null) {
			return 0;
		}
		return count.get();
	}

	public void clear() {
		this.countMap.clear();
	}
}
