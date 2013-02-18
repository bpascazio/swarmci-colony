package com.bytefly.swarm.colony.managers;

import com.bytefly.swarm.colony.SecurityContext;
import com.bytefly.swarm.colony.Status;
import com.bytefly.swarm.colony.collections.ProjectList;
import com.bytefly.swarm.colony.managers.work.Work;
import com.bytefly.swarm.colony.models.Project;
import com.bytefly.swarm.colony.util.Debug;
import com.bytefly.swarm.colony.util.HttpConnector;

public class CloudManager extends Manager {

	SecurityContext mctx = null;
	public CloudManager(SecurityContext sc) {
		mctx = sc;
	}
	
	// Default is not connected
	private boolean mconnectedState = false;

	public boolean connected() {
		return mconnectedState;
	}
	
	@Override
	public void run() {
		Debug.Log(Debug.INFO, "CloudManager started.");
		ProjectList pl = null;
		while (running) {
			Debug.Log(Debug.TRACE, "CloudManager taking from queue");
			Work w = null;
			try {
				w = this.take();
				Debug.Log(Debug.TRACE,
						"CloudManager received item " + w.toString());
				if (w.name.equals(Work.WORK_ITEM_STOP)) {
					// told to stop
					stop();
				} else if (w.name.equals(Work.WORK_ITEM_CLOUD_CHECK_CONNECTION)) {
					Debug.Log(Debug.DEBUG,
							"CloudManager checking connection to the cloud.");

					HttpConnector hc = new HttpConnector();
					boolean newConnectedState  = hc.checkConnection(mctx.getSecureEmail(), mctx.getSecurePassword());

					if (newConnectedState == true && mconnectedState == false) {
						// We went from a disconnected state to a connected.
						mTriggerLatch = true;
					}
					
					if (newConnectedState==true) Status.cloud_connected = true;

					mconnectedState = newConnectedState;
				}
			} catch (Exception e) {
				Debug.Log(Debug.INFO,
						"CloudManager work queue exception - exiting "+e);
				stop();
			}
		}
		Debug.Log(Debug.INFO, "ProjectManager stopped.");
	}	
	private boolean mTriggerLatch = false;
	
	public boolean connectTriggerLatchTransition() {
		boolean latchState = mTriggerLatch;
		if (mTriggerLatch==true) mTriggerLatch = false;
		return latchState;
	}

}
