package com.bytefly.swarm.colony.managers.work;

public class Work {
	public static final String WORK_ITEM_GENERIC = "generic-work-item";
	public static final String WORK_ITEM_STOP = "stop-work-item";
	public static final String WORK_ITEM_UPDATE_PROJECTS = "update-projects-work-item";
	public static final String WORK_ITEM_GIT_SCAN_PROJECTS = "scan-projects-work-item";
	public static final String WORK_ITEM_BUILD_BUILD_PROJECT = "build-project-work-item";
	public static final String WORK_ITEM_PROJECT_FETCH_PROJECTS = "fetch-projects-work-item";
	public String name = WORK_ITEM_GENERIC;
	public Object data;
	public Work(String n) {
		name = n;
	}
	public String toString() {
		return name;
	}
}
