package com.bytefly.swarm.colony.managers;

public class Work {
	public static final String GENERIC_WORK_ITEM = "generic-work-item";
	public static final String UPDATE_PROJECTS_WORK_ITEM = "update-projects-work-item";
	public String name = GENERIC_WORK_ITEM;
	public Object data;
	public Work(String n) {
		name = n;
	}
	public String toString() {
		return name;
	}
}
