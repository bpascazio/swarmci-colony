package com.bytefly.swarm;

import java.util.Scanner;

import com.bytefly.swarm.util.Debug;

public class CommandLine {
	public static void main(String[] args) {
		if (args.length==0) {
			usage();
		} else if (args[0].equals("version")) {
			version();
		} else if (args[0].equals("create")) {
			create();
		}
	}
	public static void usage() {
		Debug.Log(Debug.INFO, "Usage: swarm [options]");
		Debug.Log(Debug.INFO, "\nOptions:\n");
		Debug.Log(Debug.INFO, "create  # Creates a new swarm project configuration");		
		Debug.Log(Debug.INFO, "start   # Sets Started project state so builds are sent out normally");
		Debug.Log(Debug.INFO, "pause   # Pauses project so success/failures are only sent to swarm owner");
		Debug.Log(Debug.INFO, "stop    # Stops project so no more builds are run");
		Debug.Log(Debug.INFO, "status  # Displays swarm status and runs tate of current project");
		Debug.Log(Debug.INFO, "log     # Displays activity log from swarm cloud server");
		Debug.Log(Debug.INFO, "version # Displays version information");
	}
	public static void version() {
		Debug.Log(Debug.INFO, "Version is "+Version.getVersion()+" Build "+Version.getBuildNum());		
	}
	public static void create() {
		Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();	}
}
