package com.bytefly.swarm.colony.collections;

import com.bytefly.swarm.common.util.HttpConnector;
import com.bytefly.swarm.colony.models.Project;

public class ProjectList {

	public ProjectList() {
		load();
	}
	
	public void load() {
		HttpConnector hc = new HttpConnector();
		hc.getEntityList(Project.ENTITY_COLLECTION);
	}
	
	public void refresh() {
		load();
	}
}
