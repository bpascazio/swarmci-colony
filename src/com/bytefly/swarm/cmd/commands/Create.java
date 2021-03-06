package com.bytefly.swarm.cmd.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.bytefly.swarm.colony.util.HttpConnector;
import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.Debug;

public class Create extends Command {
	String repo = "";

	public Create(String n) {

		if (checkForGit()) {

			getRepo();
			if (repo == null) {
				System.out
						.print("Repository not found or not supported on this platform.\n");
				return;
			}
			checkForSwarmXML();
			Project p = new Project();
			p.Repo = repo;
			p.Name = n;
			SwarmUser sw = SwarmUser.getUserInfo();
			HttpConnector h = new HttpConnector();
			sw.uid = h.checkConnection(sw.server, sw.email, sw.password);
			if (sw.uid == 0) {
				System.out.print("Could not authenticate.\n");
			} else {
				p.UserId = sw.uid;
				h.setEntity(sw.server, p);
				System.out.print("Swarm cloud building enabled.\n");
			}
		} else {
			System.out.print("\n");
		}
	}

	public void getRepo() {
		try {
			repo = "";
			Process pr = null;
			String homeDir = System.getenv("HOMEPATH");
			if (homeDir != null && homeDir.equals("") == false) {
				repo = null;
				return;
			} else {
				pr = Runtime.getRuntime().exec("git remote show origin", null,
						new File("."));
			}
			pr.waitFor();
			String result = getOutAndErrStream(pr);
			System.out.print("result=" + result);
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
			System.out.print("git repo exception: " + e);
		}
	}
}