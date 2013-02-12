package com.bytefly.swarm.commands;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.util.Debug;
import com.bytefly.swarm.util.HttpConnector;

public class Create extends Command {
	String repo = "";

	public Create() {

		if (checkForGit()) {

			getRepo();

			Scanner scanner = new Scanner(System.in);
			System.out.print("Username: ");
			String username = scanner.nextLine();
			System.out.print("Password: ");
			String password = scanner.nextLine();
		}
		Project p = new Project();
		p.Repo=repo;
		HttpConnector h = new HttpConnector();
		h.setEntity(p);
	}

	public void getRepo() {
		try {
			repo = "";
			Process pr = Runtime.getRuntime().exec("git remote show origin",
					null, new File("."));
			pr.waitFor();
			String result = getOutAndErrStream(pr);
			Pattern myPattern = Pattern.compile(".git.",
					Pattern.CASE_INSENSITIVE);
			// loop start
			String[] lines = result.split(System.getProperty("line.separator"));
			int i;
			for (i = 0; i < lines.length; i++) {

				String in = lines[i];
				int j = in.indexOf("URL:");
				if (j > 0) {
					String path = in.substring(j + 5);
					if (Debug.verbose)
						System.out.println("*found path " + path);
					repo = path;
					return;
				}
			}
			
		} catch (Exception e) {
			if (Debug.verbose)
				System.out.print("git repo exception: " + e);
		}
	}
}
