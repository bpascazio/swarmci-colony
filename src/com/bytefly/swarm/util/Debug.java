package com.bytefly.swarm.util;

public class Debug {

	static public final int TRACE   = 0;
	static public final int DEBUG   = 1;
	static public final int WARNING = 2;
	static public final int INFO    = 3;
	static public final int ERROR   = 4;

	private static final int log_level = TRACE;

	public static void Log(int level, String msg) {
		if (level >= log_level) {
			System.out.println(msg);
		}
	}

}
