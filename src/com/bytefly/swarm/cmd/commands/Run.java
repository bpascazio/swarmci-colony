package com.bytefly.swarm.cmd.commands;

import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.collections.ProjectList;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.HttpConnector;

public class Run extends Command {
	public Run(String pn, boolean s) {
		SwarmUser sw = SwarmUser.getUserInfo();
		Config.setRailsServer(sw.server);
		HttpConnector h = new HttpConnector();
		Project fproject = null;
		boolean cc = h.checkConnection(sw.email, sw.password);
		if (cc) {
			ProjectList pl = new ProjectList();
			for (int i = 0; i < pl.cv.size(); i++) {
				Project p = (Project) pl.cv.elementAt(i);
				if (p.Name.equals(pn)) {
					fproject = p;
					break;
				}
			}
			if (fproject == null) {
				System.out.print("Project was not found.\n");
			} else {
				if (s) {
					if (fproject.buildTrigger == 0) {
						System.out.print("Project is now running.\n");
						fproject.buildTrigger = 1;
						h.updateEntity(fproject, fproject.ProjectId);
					} else {
						System.out.print("Project is already running.\n");
					}
				} else {
					if (fproject.buildTrigger == 1) {
						System.out.print("Project is now stopped.\n");
						fproject.buildTrigger = 0;
						h.updateEntity(fproject, fproject.ProjectId);
					} else {
						System.out.print("Project is not running.\n");
					}
				}
			}

		} else {
			System.out.print("Could not connect to server.\n");
		}
	}

}
