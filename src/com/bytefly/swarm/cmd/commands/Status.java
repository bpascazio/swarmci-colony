package com.bytefly.swarm.cmd.commands;

import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.collections.ProjectList;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.HttpConnector;

public class Status extends Command {
	public Status() {
			SwarmUser sw = SwarmUser.getUserInfo();
			System.out.print("Email:\t"+sw.email+"\n");
			System.out.print("Server:\t"+sw.server+"\n");
			Config.setRailsServer(sw.server);
			HttpConnector h = new HttpConnector();
			boolean cc = h.checkConnection(sw.email, sw.password);
			if (cc) {
				ProjectList pl = new ProjectList();
				for (int i=0;i<pl.cv.size();i++) {
					Project p = (Project) pl.cv.elementAt(i);
					System.out.print(p.Name+" last build "+p.buildNum+". ["+p.Repo+"]\n");
				}
			} else {
				System.out.print("Could not connect to server.\n");
			}
	}

}
