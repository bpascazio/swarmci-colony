package com.bytefly.swarm.cmd.commands;

import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.collections.ProjectList;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Config;
import com.bytefly.swarm.colony.util.HttpConnector;

public class OnOff extends Command {
	public OnOff(String pn, boolean s) {
			SwarmUser sw = SwarmUser.getUserInfo();
			//System.out.print("Email:\t"+sw.email+"\n");
			//System.out.print("Server:\t"+sw.server+"\n");
			Config.setRailsServer(sw.server);
			HttpConnector h = new HttpConnector();
			Project fproject=null;
			boolean cc = h.checkConnection(sw.email, sw.password);
			if (cc) {
				ProjectList pl = new ProjectList();
				for (int i=0;i<pl.cv.size();i++) {
					Project p = (Project) pl.cv.elementAt(i);
					//System.out.print(p.Name+" last build "+p.buildNum+". ["+p.Repo+"]\n");
					if (p.Name.equals(pn)) {
						fproject = p; break;
					}
				}
				if (fproject==null) {
					System.out.print("Project was not found.\n");
				} else {
					System.out.print("Project is now "+(s?"On":"Off")+"\n");
					fproject.buildState=s?1:0;
					h.updateEntity(fproject, fproject.ProjectId);
				}
				
			} else {
				System.out.print("Could not connect to server.\n");
			}
	}

}
