package com.bytefly.swarm.cmd.commands;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.bytefly.swarm.cmd.util.Debug;
import com.bytefly.swarm.cmd.util.HttpConnector;
import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;

public class Create extends Command {
	String repo = "";

	public Create() {

		if (checkForGit()) {

			getRepo();
			checkForSwarmXML();
			Project p = new Project();
			p.Repo=repo;
			SwarmUser sw = SwarmUser.getUserInfo();
			HttpConnector h = new HttpConnector();
			sw.uid = h.checkConnection(sw.email, sw.password);
			if (sw.uid==0) {
				System.out.print("Could not authenticated.\n");
			} else {
				p.UserId = sw.uid;
				h.setEntity(p);
				System.out.print("Swarm cloud building enabled.\n");
			}
		} else {
			System.out.print("\n");
		}
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