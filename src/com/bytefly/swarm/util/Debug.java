package com.bytefly.swarm.util;

public class Debug {

	static public final int TRACE   = 0;
	static public final int DEBUG   = 1;
	static public final int WARNING = 2;
	static public final int INFO    = 3;
	static public final int ERROR   = 4;

	private static int log_level = TRACE;

	public static boolean verbose = true;
	
	public static void setLevel(int l) {
		log_level = l;
	}

	public static void setLevel(String sl) {
		if (sl.equals("TRACE")) log_level = TRACE;
		else if (sl.equals("DEBUG")) log_level = DEBUG;
		else if (sl.equals("WARNING")) log_level = WARNING;
		else if (sl.equals("INFO")) log_level = INFO;
		else log_level = ERROR;
	}
	
	public static void Log(int level, String msg) {
		if (level >= log_level) {
			System.out.println(msg);
		}
	}

}
