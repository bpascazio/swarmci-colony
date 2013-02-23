package com.bytefly.swarm.colony.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Debug {

	static public final int ALWAYS = 0;
	static public final int TRACE = 1;
	static public final int DEBUG = 2;
	static public final int WARNING = 3;
	static public final int INFO = 4;
	static public final int ERROR = 5;

	private static int log_level = TRACE;

	public static void setLevel(int l) {
		log_level = l;
	}

	public static void setLevel(String sl) {
		if (sl.equals("TRACE"))
			log_level = TRACE;
		else if (sl.equals("DEBUG"))
			log_level = DEBUG;
		else if (sl.equals("WARNING"))
			log_level = WARNING;
		else if (sl.equals("INFO"))
			log_level = INFO;
		else
			log_level = ERROR;
	}
	
	public static String getLevel(int l) {
		switch (l) {
		case ALWAYS:return "*";
		case TRACE:return "T";
		case DEBUG:return "D";
		case WARNING:return "W";
		case INFO:return "I";
		case ERROR:return "E";
		}
		return "*";
	}

	public static void Log(String msg) {
		Loga(ALWAYS, msg);
	}

	public static void Log(int level, String msg) {
		if (level >= log_level) {
			Loga(level, msg);
		}
	}

	public static void Loga(int level, String msg) {
		if (Config.getLogFile()==null||Config.getLogFile().equals("")) {
			System.out.println("" + getLevel(level) + ":" + msg);
		} else {
			BufferedWriter bw = null;

			try {
				bw = new BufferedWriter(new FileWriter(Config.getLogFile(),
						true));
				bw.write("" + getLevel(level) + ":" + msg);
				bw.newLine();
				bw.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally { // always close the file
				if (bw != null) {
					try {
						bw.close();
					} catch (IOException ioe2) {
						// just ignore it
					}
				}
			}

		}
	}

}
