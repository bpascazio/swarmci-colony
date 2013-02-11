package com.bytefly.swarm.colony.models;

public class Project extends Entity{
	public final String ENTITY = "Project";
	public final String ENTITY_COLLECTION = "projects";
	public String Name;
	public int UserId;
	public int ProjectId;
	public String Repo;
	public int BuilderType;
	public String BaseName;
	public boolean forceClean;
	public boolean debug;
	public String Version;
	public int buildNum;
	public boolean triggerBuild;
	public String buildDirectory;
	
	public Project() {
		forceClean = false;
		debug = true;
		Version = "1.0";
		buildNum = 1;
		triggerBuild = false;
		buildDirectory = "";
		UserId = -1;
		ProjectId = -1;
	}
}
