package com.bytefly.swarm.colony.managers;

import java.util.Queue;
import java.util.LinkedList;

import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.util.Config;

public abstract class Manager implements Runnable {

	protected boolean running;
	protected Queue<Work> workq;
	private int capacity;

	Manager() {
		this(true);
	}
	
	int getQSize() {
		if (workq!=null) {
			return workq.size();
		}
		
		// Return -1 to flag this as invalid data, not 0 which is valid.
		return -1;
	}

	Manager(boolean autostart) {
		workq = new LinkedList<Work>();
		if (autostart) {
			start();
			create();
		} else {
			running = false;
		}
		capacity = Config.getIntValue(Config.SWARM_DEFAULT_QUEUE_SIZE);
	}

	public synchronized void put(Work element) throws InterruptedException {
		while (workq.size() == capacity) {
			wait();
		}

		workq.add(element);
		notify();
	}

	public synchronized Work take() throws InterruptedException {
		while (workq.isEmpty()) {
			wait();
		}

		Work item = workq.remove();
		notify();
		return item;
	}

	void stop() {
		running = false;
	}

	void start() {
		running = true;
	}

	public Thread create() {
		Thread t = new Thread(this);
		t.start();
		return t;
	}

	abstract public void run();
}
