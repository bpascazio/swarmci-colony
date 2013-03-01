package com.bytefly.swarm.cmd;

import java.util.Scanner;

import com.bytefly.swarm.cmd.commands.Create;
import com.bytefly.swarm.cmd.commands.Log;
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
			if (args.length>1) {
				create(args[1]);
			} else {
				usage();
				Debug.Log(Debug.INFO, ">>>>>>>>>> Project name is required such as 'swarm create myapp'.");
			}
		} else if (args[0].equals("log")) {
			if (args.length>1) {
				log(args[1]);
			} else {
				usage();
				Debug.Log(Debug.INFO, ">>>>>>>>>> Project name is required such as 'swarm log myproject'.");
			}
		} else if (args[0].equals("status")) {
			status();
		}
	}
	public static void usage() {
		Debug.Log(Debug.INFO, "\nCloud Based Continuous Integration for Mobile Apps.");
		Debug.Log(Debug.INFO, "\nUsage:\n\tswarm [options] <project name> <profile filename> <comment>");
		Debug.Log(Debug.INFO, "\nDescription: To begin using swarm run 'swarm create <project name>' while in\nthe subdirectory containing your git repository.  The project will initially\nbe in the off state and not compile builds on git commits.");
		Debug.Log(Debug.INFO, "\nFor build settings, review the swarm.xml which should be created in the repository\nsubdirectory.");
		Debug.Log(Debug.INFO, "\nNext use 'swarm run <project name> to test the cloud compilation. After successful");
		Debug.Log(Debug.INFO, "compilation run 'swarm on <project name>' to begin normal continuous integration.");
		Debug.Log(Debug.INFO, "\nOptions:\n");
		Debug.Log(Debug.INFO, "create <project name> - Create and configure a new project.");		
		Debug.Log(Debug.INFO, "remove <project name> - Removes a project from swarm.");		
		Debug.Log(Debug.INFO, "profile <project name> <profile filename> - Uploads Apple iOS profile to swarm cloud.");
		Debug.Log(Debug.INFO, "run    <project name> - Run a single build of the project.");
		Debug.Log(Debug.INFO, "on     <project name> - Start normal continuous integration for project.");
		Debug.Log(Debug.INFO, "off    <project name> - Stops continous integration.");
		Debug.Log(Debug.INFO, "\nstatus  - Displays list of projects and their status.");
		Debug.Log(Debug.INFO, "log     - Displays log file for a project.");
		Debug.Log(Debug.INFO, "version - Displays version information.\n");
	}
	public static void version() {
		Debug.Log(Debug.INFO, "Version is "+Info.build_version+" built on "+Info.build_date);		
	}
	public static void create(String n) {
		Create c = new Create(n);
	}
	public static void log(String n) {
		Log c = new Log(n);
	}
	public static void status() {
		Status c = new Status();
	}
}
