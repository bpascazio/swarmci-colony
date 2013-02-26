package com.bytefly.swarm.colony.models;

import com.bytefly.swarm.colony.util.Debug;

public class Entity {
	public String ENTITY;
	public String ENTITY_COLLECTION;
	private boolean busy;

	public synchronized void setBusy(String p, boolean _busy) {
		busy = _busy;
		Debug.Log(Debug.TRACE, "going to busy state " + p + " " + busy);
	}

	public synchronized boolean getBusy() {
		Debug.Log(Debug.TRACE, "getting busy state " + busy);
		return busy;
	}
}
