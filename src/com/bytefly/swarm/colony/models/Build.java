package com.bytefly.swarm.colony.models;

public class Build extends Entity {
	public int project_id;
	public int user_id;
	public boolean success;
	
	public Build() {
		ENTITY = "Build";
		ENTITY_COLLECTION = "builds";
		project_id = -1;
		user_id = -1;
		success = false;
	}
}
