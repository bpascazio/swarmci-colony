package com.bytefly.swarm.colony.models;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class Entity {
	public String ENTITY;
	public String ENTITY_COLLECTION;
	private boolean busy = false;
	private long busy_timestamp = 0;

	public Entity() {
		busy_timestamp = System.currentTimeMillis();
	}
	
	public synchronized void setBusy(String p, boolean _busy) {
		busy = _busy;
		Debug.Log(Debug.TRACE, "going to busy state " + p + " " + busy);
		busy_timestamp = System.currentTimeMillis();
	}

	public synchronized boolean getBusy() {
		Debug.Log(Debug.TRACE, "getting busy state " + busy);
		if (busy && (System.currentTimeMillis()-busy_timestamp)>Config.getIntValue(Config.SWARM_COLONY_MAX_BUSY)) {
			busy = false;
			Status.counter_zombie_projects++;
			Debug.Log(Debug.WARNING, "entity became a zombie " + busy);
		}
		return busy;
	}
}
