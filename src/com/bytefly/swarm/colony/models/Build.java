package com.bytefly.swarm.colony.models;

public class Build extends Entity {
	public String created_at;
	public int project_id;
	public boolean success;
	public int user_id;
	public String logs;
	public String info;
	public String project_name;
	public int bldnum;

	public Build() {
		ENTITY = "Build";
		ENTITY_COLLECTION = "builds";
		created_at = "";
		project_id = -1;
		success = false;
		user_id = -1;
		logs = "";
		info = "";
		project_name = "";
		bldnum = 0;
	}
}
