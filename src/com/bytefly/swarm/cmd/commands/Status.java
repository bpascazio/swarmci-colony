package com.bytefly.swarm.cmd.commands;

import com.bytefly.swarm.cmd.util.HttpConnector;
import com.bytefly.swarm.cmd.util.SwarmUser;
import com.bytefly.swarm.colony.models.Project;

public class Status extends Command {
	public Status() {
			SwarmUser sw = SwarmUser.getUserInfo();
			System.out.print("Email:\t"+sw.email+"\n");
			System.out.print("Server:\t"+sw.server+"\n");
	}

}
