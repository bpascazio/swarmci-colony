package com.bytefly.swarm.colony.collections;

import java.util.Vector;

import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.models.Entity;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;

public class ProjectList extends Collection {

	public ProjectList() {
		load();
	}

	public void load() {
		try {
			HttpConnector hc = new HttpConnector();
			this.cv = hc.getEntityList(new Project().ENTITY_COLLECTION);
			if (this.cv == null) {
				this.cv = new Vector<Entity>();
				this.valid = false;
				Status.counter_cloud_comm_failure++;
			} else {
				this.valid = true;
				Status.counter_cloud_comm_success++;
			}
		} catch (Exception e) {
			Debug.Log(Debug.INFO, "ProjectList load X " + e);
		}
	}

	public void refresh() {
		load();
	}
}
