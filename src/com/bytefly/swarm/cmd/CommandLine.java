package com.bytefly.swarm.cmd;

import java.util.Scanner;

import com.bytefly.swarm.cmd.commands.Create;
import com.bytefly.swarm.cmd.commands.Log;
import com.bytefly.swarm.cmd.commands.OnOff;
import com.bytefly.swarm.cmd.commands.Run;
import com.bytefly.swarm.cmd.commands.Status;
import com.bytefly.swarm.colony.Info;
import com.bytefly.swarm.colony.util.Debug;

public class CommandLine {
	public static void main(String[] args) {
		Debug.logSurpress();
		if (args.length==0) {
			usage();
		} else if (args[0].equals("version")) {
			version();
		} else if (args[0].equals("create")) {
			if (args.length>1) {
				create(args[1]);
			} else {
				usage();
				System.out.println(">>>>>>>>>> Project name is required such as 'swarm create myapp'.");
			}
		} else if (args[0].equals("on")) {
			if (args.length>1) {
				on_off(args[1],true);
			} else {
				usage();
				System.out.println(">>>>>>>>>> Project name is required such as 'swarm on myapp'.");
			}
		} else if (args[0].equals("off")) {
			if (args.length>1) {
				on_off(args[1],false);
			} else {
				usage();
				System.out.println(">>>>>>>>>> Project name is required such as 'swarm off myapp'.");
			}
		} else if (args[0].equals("run")) {
			if (args.length>1) {
				run(args[1],true);
			} else {
				usage();
				System.out.println(">>>>>>>>>> Project name is required such as 'swarm create myapp'.");
			}
		} else if (args[0].equals("log")) {
			if (args.length>1) {
				log(args[1]);
			} else {
				usage();
				System.out.println(">>>>>>>>>> Project name is required such as 'swarm log myproject'.");
			}
		} else if (args[0].equals("status")) {
			status();
		}
	}
	public static void usage() {
		System.out.println("\nCloud Based Continuous Integration for Mobile Apps.");
		System.out.println("\nUsage:\n\tswarm [options] <project name> <profile filename> <comment>");
		System.out.println("\nDescription: To begin using swarm run 'swarm create <project name>' while in\nthe subdirectory containing your git repository.  The project will initially\nbe in the off state and not compile builds on git commits.");
		System.out.println("\nFor build settings, review the swarm.xml which should be created in the repository\nsubdirectory.");
		System.out.println("\nNext use 'swarm run <project name> to test the cloud compilation. After successful");
		System.out.println("compilation run 'swarm on <project name>' to begin normal continuous integration.");
		System.out.println("\nOptions:\n");
		System.out.println("create <project name> - Create and configure a new project.");		
		System.out.println("remove <project name> - Removes a project from swarm.");		
		System.out.println("profile <project name> <profile filename> - Uploads Apple iOS profile to swarm cloud.");
		System.out.println("run    <project name> - Run a single build of the project.");
		System.out.println("stop   <project name> - Stop a running project.");
		System.out.println("on     <project name> - Start normal continuous integration for project.");
		System.out.println("off    <project name> - Stops continous integration.");
		System.out.println("\nstatus  - Displays list of projects and their status.");
		System.out.println("log     - Displays log file for a project.");
		System.out.println("version - Displays version information.\n");
	}
	public static void version() {
		System.out.println("Version is "+Info.build_version+" built on "+Info.build_date);		
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
	public static void on_off(String n, boolean s) {
		OnOff c = new OnOff(n,s);
	}
	public static void run(String n, boolean s) {
		Run c = new Run(n,s);
	}
}
