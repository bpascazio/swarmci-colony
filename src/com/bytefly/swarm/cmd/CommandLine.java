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
			if (args.length>1) {
				create(args[1]);
			} else {
				Debug.Log(Debug.INFO, "\n>>>>>>>>>> Project name is required such as 'swarm create myapp'.");
				usage();
			}
		} else if (args[0].equals("status")) {
			status();
		}
	}
	public static void usage() {
		Debug.Log(Debug.INFO, "\nCloud Based Continuous Integration for Mobile Apps.");
		Debug.Log(Debug.INFO, "\nUsage:\n\tswarm [options] <project name> <profile filename> <comment>");
		Debug.Log(Debug.INFO, "\nDescription: To begin using swarm run 'swarm create <project name>' while in the subdirectory\ncontaining your git repository.  The project will initially be in the paused state (only you get emails).");
		Debug.Log(Debug.INFO, "For build settings review the swarm.xml which is created in the repository subdirectory.");
		Debug.Log(Debug.INFO, "Initially the project is set to compile on a push to the remote git repository. After successful");
		Debug.Log(Debug.INFO, "compilation run 'swarm start <project name>' to begin normal continuous integration.");
		Debug.Log(Debug.INFO, "\nOptions:\n");
		Debug.Log(Debug.INFO, "create <project name> - Create and configure a new project.");		
		Debug.Log(Debug.INFO, "remove <project name> - Removes a project from swarm.");		
		Debug.Log(Debug.INFO, "profile <project name> <profile filename> - Uploads Apple iOS profile to swarm cloud.");
		Debug.Log(Debug.INFO, "\nstart <project name> - Start normal continuous integration for project. ");
		Debug.Log(Debug.INFO, "stop  <project name> - Stops continuous integration running.");
		Debug.Log(Debug.INFO, "pause <project name> - Paused state, build results only sent to current swarm user.");
		Debug.Log(Debug.INFO, "\ntest  <project name> <comment> - Run a test build and send results only to current swarm user.");
		Debug.Log(Debug.INFO, "build <project name> <comment> - Run a full build and distribute normally.");
		Debug.Log(Debug.INFO, "\n<comment> must be a put in \"quotes\"");
		Debug.Log(Debug.INFO, "\nlog     - Displays log file for a project.");
		Debug.Log(Debug.INFO, "status  - Displays list of projects and their status.");
		Debug.Log(Debug.INFO, "version - Displays version information.\n");
	}
	public static void version() {
		Debug.Log(Debug.INFO, "Version is "+Info.build_version+" built on "+Info.build_date);		
	}
	public static void create(String n) {
		Create c = new Create(n);
	}
	public static void status() {
		Status c = new Status();
	}
}
