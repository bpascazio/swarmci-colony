package com.bytefly.swarm.colony;

public class Status {

	public static int counter_git_clone = 0;
	public static int counter_git_updates = 0;
	public static int counter_git_checks = 0;
	public static int counter_git_repocleans = 0;
	public static int counter_loaded_projects = 0;
	public static int counter_builds_total = 0;
	public static int counter_builds_xcode = 0;
	public static int counter_builds_android = 0;
	public static int counter_builds_triggered = 0;
	public static int counter_builds_success = 0;
	public static int counter_builds_failure = 0;
	public static int counter_heartbeat = 0;
	public static int counter_zombie_projects = 0;
	
	public static long counter_initial_uptime = 0;
	public static String[] project_list = null;
	public static boolean cloud_connected = false;
	public static String cloud_address = "";
	public static boolean colony_alive = true;
	
	public static int qsize_build_mgr = -1;
	public static int qsize_cloud_mgr = -1;
	public static int qsize_git_mgr = -1;
	public static int qsize_proj_mgr = -1;
}
