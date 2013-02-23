package com.bytefly.swarm.colony.models;

public class Build extends Entity {
	public String created_at;
	public int project_id;
	public boolean success;
	public int user_id;
	
	public Build() {
		ENTITY = "Build";
		ENTITY_COLLECTION = "builds";
		created_at = "";
		project_id = -1;
		success = false;
		user_id = -1;
	}
}
