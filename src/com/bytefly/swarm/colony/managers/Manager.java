package com.bytefly.swarm.colony.managers;

public abstract class Manager implements Runnable {

	protected boolean running;

	Manager() {
		this(true);
	}

	Manager(boolean autostart) {
		if (autostart) {
			running = true;
			create();
		} else {
			running = false;
		}
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
