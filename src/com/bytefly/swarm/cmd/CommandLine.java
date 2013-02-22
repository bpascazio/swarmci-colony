package com.bytefly.swarm.cmd;

import java.util.Scanner;

import com.bytefly.swarm.cmd.commands.Create;
import com.bytefly.swarm.cmd.commands.Status;
import com.bytefly.swarm.cmd.util.Debug;
import com.bytefly.swarm.colony.Info;

public class CommandLine {
	public static void main(String[] args) {
		if (args.length==0) {
			usage();
		} else if (args[0].equals("version")) {
			version();
		} else if (args[0].equals("create")) {
			create();
		} else if (args[0].equals("status")) {
			status();
		}
	}
	public static void usage() {
		Debug.Log(Debug.INFO, "Usage:\n\tswarm [options] <project name>");
		Debug.Log(Debug.INFO, "\nOptions:\n");
		Debug.Log(Debug.INFO, "create  - Create and configure a new project.");		
		Debug.Log(Debug.INFO, "start   - Start normal continuous integration for project. ");
		Debug.Log(Debug.INFO, "pause   - Paused state, build results only sent to swarm user address.");
		Debug.Log(Debug.INFO, "stop    - No continuous integration running.");
		Debug.Log(Debug.INFO, "status  - Displays list of projects and their status.");
		Debug.Log(Debug.INFO, "log     - Displays log file with build results of a project.");
		Debug.Log(Debug.INFO, "version - Displays version information");
	}
	public static void version() {
		Debug.Log(Debug.INFO, "Version is "+Info.build_version+" built on "+Info.build_date);		
	}
	public static void create() {
		Create c = new Create();
	}
	public static void status() {
		Status c = new Status();
	}
}
