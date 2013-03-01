package com.bytefly.swarm.colony.models;

public class Project extends Entity{
	public String Name;
	public int UserId;
	public int ProjectId;
	public String Repo;
	public int BuilderType;
	public String BaseName;
	public String BaseNameMinimal;
	public boolean forceClean;
	public boolean debug;
	public String Version;
	public int buildNum;
	public boolean triggerBuild;
	public String buildDirectory;
	public Logfile logFile;
	public String commit;
	public String reason;
	public int buildState;
	public int buildTrigger;
	
	public Project() {
		ENTITY = "Project";
		ENTITY_COLLECTION = "projects";
		forceClean = false;
		debug = true;
		Version = "1.0";
		buildNum = 1;
		triggerBuild = false;
		buildDirectory = "";
		UserId = -1;
		ProjectId = -1;
		BaseNameMinimal = "";
		logFile = null;
		commit = "";
		reason = "";
		buildState = 0;
		buildTrigger = 0;
		this.setBusy("", false);
	}
}
