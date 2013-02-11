package com.bytefly.swarm.colony.util;

import com.bytefly.swarm.colony.util.Debug;

public class Config {
	public static final String SWARM_PROJECT_CHECK_FREQ = "SWARM_PROJECT_CHECK_FREQ";
	public static final String SWARM_GIT_CHECK_FREQ = "SWARM_GIT_CHECK_FREQ";
	public static final String SWARM_MGR_CHECK_FREQ = "SWARM_MGR_CHECK_FREQ";
	public static final String SWARM_RAILS_URL = "SWARM_RAILS_URL";
	public static final String SWARM_DEFAULT_QUEUE_SIZE = "SWARM_DEFAULT_QUEUE_SIZE";
	public static final String SWARM_GIT_CLONE_CMD = "SWARM_GIT_CLONE_CMD";
	public static final String SWARM_XCODE_BUILD_CMD = "SWARM_XCODE_BUILD_CMD";
	public static final String SWARM_GIT_CHECK_CMD = "SWARM_GIT_CHECK_CMD";
	public static final String SWARM_ANDROID_APP_NAME = "SWARM_ANDROID_APP_NAME";
	public static final String SWARM_GIT_UPDATE_CMD = "SWARM_GIT_UPDATE_CMD";
	public static final String SWARM_CLEAN_REPO_CMD = "SWARM_CLEAN_REPO_CMD";
	public static final String SWARM_NOTIFY_EMAIL_CMD = "SWARM_NOTIFY_EMAIL_CMD";
	public static final String SWARM_ANDROID_BUILD_CMD = "SWARM_ANDROID_BUILD_CMD";
	public static final String SWARM_ANDROID_CLEARN_CMD = "SWARM_ANDROID_CLEARN_CMD";
	public static final String SWARM_ANDROID_UPLOAD_APK = "SWARM_ANDROID_UPLOAD_APK";
	public static final String SWARM_ANDROID_FIND_MANIFEST = "SWARM_ANDROID_FIND_MANIFEST";
	public static final String SWARM_ANDROID_FIND_BUILDXML = "SWARM_ANDROID_FIND_BUILDXML";
	public static final String SWARM_ANDROID_SEND_EMAIL_APK = "SWARM_ANDROID_SEND_EMAIL_APK";
	public static final String SWARM_SEND_FAILURE_EMAIL = "SWARM_SEND_FAILURE_EMAIL";
	public static final String SWARM_DUMP_XML = "SWARM_DUMP_XML";
	
	public static int getIntValue(String key) {
		if (key.equals(SWARM_PROJECT_CHECK_FREQ)) {
			return 60000; // fetch projects from rails server every 60 seconds
		}
		if (key.equals(SWARM_GIT_CHECK_FREQ)) {
			return 30000; // scan for updated git repositories every 15 seconds
		}
		if (key.equals(SWARM_MGR_CHECK_FREQ)) {
			return 250; // swarm manager checks for commands every 4 times a second
		}
		if (key.equals(SWARM_DEFAULT_QUEUE_SIZE)) {
			return 25; // default maximum work items in queue
		}		
		Debug.Log(Debug.DEBUG, "Undefined Config Value " + key);
		return 0;
	}

	public static String getStringValue(String key) {
		if (key.equals(SWARM_RAILS_URL)) {
			return "http://swarm-cloud.herokuapp.com/";
		}
		if (key.equals(SWARM_GIT_UPDATE_CMD)) {
			return "git pull";
		}
		if (key.equals(SWARM_GIT_CLONE_CMD)) {
			return "git clone";
		}
		if (key.equals(SWARM_XCODE_BUILD_CMD)) {
			return "/usr/bin/xcodebuild -sdk iphoneos6.0 -configuration Release OBJROOT=\"build_intermediates\" SYMROOT=\"build_results\" IPHONEOS_DEPLOYMENT_TARGET=5.0 clean build";
		}
		if (key.equals(SWARM_ANDROID_BUILD_CMD)) {
			return "ant";
		}	
		if (key.equals(SWARM_GIT_CHECK_CMD)) {
			return "git rev-parse HEAD";
		}
		if (key.equals(SWARM_CLEAN_REPO_CMD)) {
			return "rm -rf";
		}
		if (key.equals(SWARM_NOTIFY_EMAIL_CMD)) {
			return "http://www.bytefly.com/apps/teamcity/email.php?name=639Building&bnum=4&build=639building-android-1.0.5.apk&log=buildlog-5.log&owner=bytefly&repo=639building-android&to=bob@bytefly.com&fname=AndroidBuild";
		}
		if (key.equals(SWARM_ANDROID_CLEARN_CMD)) {
			return "rm -rf bin";
		}
		if (key.equals(SWARM_ANDROID_FIND_MANIFEST)) {
			return "find . -name AndroidManifest.xml -print -quit";
		}
		if (key.equals(SWARM_ANDROID_FIND_BUILDXML)) {
			return "find . -name build.xml -print -quit";
		}
		if (key.equals(SWARM_ANDROID_APP_NAME)) {
			return "find . -name *.apk -print -quit";
		}
		if (key.equals(SWARM_ANDROID_UPLOAD_APK)) {
			return "scp -P 22123 %s bpascazio@www.bytefly.com:builds/%s";
		}
		if (key.equals(SWARM_ANDROID_SEND_EMAIL_APK)) {
			return "curl http://www.bytefly.com/apps/teamcity/email.php?name=%s&bnum=%d&build=%s&log=buildlog-5.log&owner=%s&repo=%s&to=%s&fname=AndroidBuild";
		}	
		if (key.equals(SWARM_SEND_FAILURE_EMAIL)) {
			return "curl http://www.bytefly.com/apps/teamcity/femail.php?name=%s&bnum=%d&build=%s&log=buildlog-5.log&owner=%s&repo=%s&to=%s&fname=AndroidBuildFailure";
		}	
		if (key.equals(SWARM_DUMP_XML)) {
			return "cat swarm.xml";
		}		
		Debug.Log(Debug.DEBUG, "Undefined Config Value " + key);
		return "";
	}
}
