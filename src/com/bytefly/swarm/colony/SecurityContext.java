package com.bytefly.swarm.colony;

import com.bytefly.swarm.cmd.util.SwarmUser;

public class SecurityContext {
	private SwarmUser mUser;
	SecurityContext() {
		mUser = SwarmUser.getUserInfo();
	}
	public String getSecureEmail() {
		return mUser.email;
	}
	public String getSecurePassword() {
		return mUser.password;
	}
}
