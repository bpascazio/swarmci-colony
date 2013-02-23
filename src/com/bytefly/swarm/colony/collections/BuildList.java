package com.bytefly.swarm.colony.collections;

import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.util.HttpConnector;

public class BuildList extends Collection {

	public BuildList() {
		load();
	}
	
	public void load() {
		HttpConnector hc = new HttpConnector();
		this.cv = hc.getEntityList(new Build().ENTITY_COLLECTION);
	}
	
	public void refresh() {
		load();
	}
}
