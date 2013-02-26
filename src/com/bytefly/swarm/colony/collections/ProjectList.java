package com.bytefly.swarm.colony.collections;

import java.util.Vector;

import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.HttpConnector;

public class ProjectList extends Collection {

	public ProjectList() {
		load();
	}
	
	public void load() {
		HttpConnector hc = new HttpConnector();
		this.cv = hc.getEntityList(new Project().ENTITY_COLLECTION);
		if (this.cv==null) {
			this.cv = new Vector<Entity>();
			this.valid = false;
		} else {
			this.valid = true;
		}
	}
	
	public void refresh() {
		load();
	}
}
