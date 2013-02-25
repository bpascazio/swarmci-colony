package com.bytefly.swarm.colony.webserver;

import java.io.DataOutputStream;

import com.bytefly.swarm.colony.Info;
import com.bytefly.swarm.colony.Status;

public class SuperColonyServerStatusPage {
	public static void sendStatus(DataOutputStream os) {
		try {
			os.writeBytes("<html>\n");
			os.writeBytes("<head><meta http-equiv=\"refresh\" content=\"5\" ></head>\n");
			os.writeBytes("<body>\n");
			os.writeBytes("<b>SUPER COLONY Server Status Page</b>\n");
			String u = ""
					+ (System.currentTimeMillis() - Status.counter_initial_uptime)
					/ 1000;
			os.writeBytes("<p><table border=\"0\">\n");
			os.writeBytes("<tr><td>Version</td><td>" + Info.build_version
					+ "</td></tr>\n");
			os.writeBytes("<tr><td>Build Date</td><td>" + Info.build_date
					+ "</td></tr>\n");
			os.writeBytes("<tr><td>Uptime</td><td>" + u
					+ " seconds</td></tr>\n");
			os.writeBytes("<tr><td>Ticker</td><td>" + Status.counter_heartbeat
					+ "</td></tr>\n");		
			os.writeBytes("</table>\n");

			os.writeBytes("</body>\n");
			os.writeBytes("</html>\n");
		} catch (Exception e) {

		}
	}

}
