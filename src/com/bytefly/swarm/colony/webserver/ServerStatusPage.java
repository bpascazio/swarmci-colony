package com.bytefly.swarm.colony.webserver;

import java.io.DataOutputStream;

import com.bytefly.swarm.colony.Info;
import com.bytefly.swarm.colony.Status;

public class ServerStatusPage {

	public static void sendStatus(DataOutputStream os) {
		try {
			os.writeBytes("<html>\n");
			os.writeBytes("<head><meta http-equiv=\"refresh\" content=\"5\" ></head>\n");
			os.writeBytes("<body>\n");
			os.writeBytes("<b>Colony Server Status Page</b>\n");
			String u = ""+(System.currentTimeMillis()-Status.counter_initial_uptime)/1000;
			os.writeBytes("<p><table border=\"0\">\n");
			os.writeBytes("<tr><td>Version</td><td>"+Info.build_version+"</td></tr>\n");
			os.writeBytes("<tr><td>Build Date</td><td>"+Info.build_date+"</td></tr>\n");
			os.writeBytes("<tr><td>Uptime</td><td>"+u+" seconds</td></tr>\n");
			os.writeBytes("</table>\n");
			
			os.writeBytes("<p><table border=\"1\">\n");
			os.writeBytes("<tr><td>Description</td><td>Count</tc></tr>\n");
			os.writeBytes("<tr><td>Loaded Projects</td><td>"+Status.counter_loaded_projects+"</td></tr>\n");
			os.writeBytes("<tr><td>Git Checks</td><td>"+Status.counter_git_checks+"</td></tr>\n");			
			os.writeBytes("<tr><td>Git Updates</td><td>"+Status.counter_git_updates+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Clone</td><td>"+Status.counter_git_clone+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git RepoCleans</td><td>"+Status.counter_git_repocleans+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Builds Total</td><td>"+Status.counter_builds_total+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Builds XCode</td><td>"+Status.counter_builds_xcode+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Builds Android</td><td>"+Status.counter_builds_android+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Builds Triggered</td><td>"+Status.counter_builds_triggered+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Builds Success</td><td>"+Status.counter_builds_success+"</td></tr>\n");	
			os.writeBytes("<tr><td>Git Builds Failure</td><td>"+Status.counter_builds_failure+"</td></tr>\n");		
			os.writeBytes("</table>\n");
			os.writeBytes("</body>\n");
			os.writeBytes("</html>\n");
		} catch (Exception e) {
			
		}
	}
}
