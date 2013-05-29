package com.bytefly.swarm.cmd.commands;

import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.collections.BuildList;
import com.bytefly.swarm.colony.models.Build;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.HttpConnector;

public class Log extends Command {
	public Log(String n) {
		SwarmUser sw = SwarmUser.getUserInfo();
		Config.setRailsServer(sw.server);
		HttpConnector h = new HttpConnector();
		boolean cc = h.checkConnection(sw.email, sw.password);
		if (cc) {
			BuildList bl = new BuildList();
			for (int i = 0; i < bl.cv.size(); i++) {
				Build b = (Build) bl.cv.elementAt(i);
				System.out.print(b.created_at + " project " + b.project_id
						+ " status " + b.success + ".\n");
			}
		} else {
			System.out.print("Could not connect to server.\n");
		}
	}

}
