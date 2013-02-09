package com.bytefly.swarm.colony.collections;

import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.util.HttpConnector;

public class ProjectList extends Collection {

	public ProjectList() {
		load();
	}
	
	public void load() {
		HttpConnector hc = new HttpConnector();
		this.cv = hc.getEntityList(new Project().ENTITY_COLLECTION);
	}
	
	public void refresh() {
		load();
	}
}
