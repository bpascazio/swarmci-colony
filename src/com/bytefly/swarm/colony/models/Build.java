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
	public int bldtype;
	
	public static final int BUILD_TYPE_UNKNOWN = 0;
	public static final int BUILD_TYPE_ANDROID = 1;
	public static final int BUILD_TYPE_IOS = 2;

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
		bldtype = BUILD_TYPE_UNKNOWN;
	}
}
