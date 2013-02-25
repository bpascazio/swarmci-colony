package com.bytefly.swarm.colony.util;

import com.bytefly.swarm.colony.util.Debug;

public class Config {
	public static final String SWARM_PROJECT_CHECK_FREQ = "SWARM_PROJECT_CHECK_FREQ";
	public static final String SWARM_GIT_CHECK_FREQ = "SWARM_GIT_CHECK_FREQ";
	public static final String SWARM_MGR_CHECK_FREQ = "SWARM_MGR_CHECK_FREQ";
	public static final String SWARM_RAILS_URL = "swarm_cloud_url";
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
	public static final String SWARM_UPLOAD_LOGFILE = "SWARM_UPLOAD_LOGFILE";
	public static final String SWARM_SEND_FAILURE_EMAIL = "SWARM_SEND_FAILURE_EMAIL";
	public static final String SWARM_DUMP_XML = "SWARM_DUMP_XML";
	public static final String SWARM_MAKE_PROJECT_DIR = "SWARM_MAKE_PROJECT_DIR";
	public static final String SWARM_MAKE_DOT_SWARM_DIR = "SWARM_MAKE_DOT_SWARM_DIR";
	public static final String SWARM_ANDROID_GENERATE_BUILDXML = "SWARM_ANDROID_GENERATE_BUILDXML";
	public static final String SWARM_CLOUD_CHECK_FREQ = "SWARM_CLOUD_CHECK_FREQ";
	public static final String SWARM_STATUS_CHECK_FREQ = "SWARM_STATUS_CHECK_FREQ";
	public static final String SWARM_COLONY_AUTHENTICATION_V1 = "SWARM_COLONY_AUTHENTICATION_V1";
	public static final String SWARM_COLONY_AUTHENTICATION_TOKEN = "SWARM_COLONY_AUTHENTICATION_TOKEN";
	public static final String SWARM_DEBUG_LOG_LEVEL = "swarm_debug_level";
	public static final String SWARM_COLONY_UUID = "swarm_colony_uuid";
	public static final String SWARM_DEBUG_LOG_FILE = "swarm_debug_file";
	public static final String SWARM_PROJECT_DIR = "swarm_project_dir";
	public static final String SWARM_ANDROID_SDK = "swarm_android_sdk";
	public static final String SWARM_COLONY_CONFIG = "swarm_default_cfg";
	public static final String SWARM_SUPERCOLONY_MODE = "swarm_supercolony_mode";
	
	public static final String SWARM_COLONY_CONFIG_PATH = "SWARM_COLONY_CONFIG_PATH";
	
	
	public static int getIntValue(String key) {
		if (key.equals(SWARM_PROJECT_CHECK_FREQ)) {
			return 30000; // fetch projects from rails server every 60 seconds
		}
		if (key.equals(SWARM_CLOUD_CHECK_FREQ)) {
			return 45000; // check connection to the cloud every 45 seconds
		}
		if (key.equals(SWARM_GIT_CHECK_FREQ)) {
			return 15000; // scan for updated git repositories every 15 seconds
		}
		if (key.equals(SWARM_STATUS_CHECK_FREQ)) {
			return 15000; // check server status 15 seconds
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
	
	static String VAL_SWARM_RAILS_URL = "swarmbeta.herokuapp.com";
	static String VAL_SWARM_COLONY_UUID = "";
	static String VAL_SWARM_DEBUG_LOG_FILE = "";
	static String VAL_SWARM_PROJECT_DIR = ".";
	static String VAL_SWARM_ANDROID_SDK = "/Android/sdk";
	static String VAL_SWARM_COLONY_CONFIG_PATH = "";
	static String VAL_SWARM_SUPERCOLONY_MODE = "";
	
	public static void setSuperColonyMode(String s) {
		VAL_SWARM_SUPERCOLONY_MODE = s;
	}
	
	public static void setConfigPath(String s) {
		VAL_SWARM_COLONY_CONFIG_PATH = s;
	}
	
	public static void setProjectDir(String s) {
		VAL_SWARM_PROJECT_DIR = s;
	}

	public static String getProjectDir() {
		return VAL_SWARM_PROJECT_DIR;
	}

	public static void setRailsServer(String s) {
		VAL_SWARM_RAILS_URL = s;
	}

	public static String getRailsServer() {
		return VAL_SWARM_RAILS_URL;
	}

	public static void setColonyUUID(String s) {
		VAL_SWARM_COLONY_UUID = s;
	}

	public static void setAndroidSDK(String s) {
		VAL_SWARM_ANDROID_SDK = s;
	}

	public static String getSuperColonyMode() {
		return VAL_SWARM_SUPERCOLONY_MODE;
	}

	public static String getAndroidSDK() {
		return VAL_SWARM_ANDROID_SDK;
	}

	public static String getColonyUUID() {
		return VAL_SWARM_COLONY_UUID;
	}

	public static void setLogFile(String s) {
		VAL_SWARM_DEBUG_LOG_FILE = s;
	}

	public static String getLogFile() {
		return VAL_SWARM_DEBUG_LOG_FILE;
	}
	
	public static String getStringValue(String key) {
		
		if (key.equals(SWARM_SUPERCOLONY_MODE)) {
			return VAL_SWARM_SUPERCOLONY_MODE;
		}
		if (key.equals(SWARM_COLONY_CONFIG)) {
			return SWARM_COLONY_CONFIG;
		}
		if (key.equals(SWARM_RAILS_URL)) {
			return VAL_SWARM_RAILS_URL;
		}
		else if (key.equals(SWARM_COLONY_UUID)) {
			return VAL_SWARM_COLONY_UUID;
		}
		else if (key.equals(SWARM_COLONY_CONFIG_PATH)) {
			return VAL_SWARM_COLONY_CONFIG_PATH;
		}
		else if (key.equals(SWARM_GIT_UPDATE_CMD)) {
			return "git pull";
		}
		else if (key.equals(SWARM_GIT_CLONE_CMD)) {
			return "git clone";
		}
		else if (key.equals(SWARM_XCODE_BUILD_CMD)) {
			return "/usr/bin/xcodebuild -sdk iphoneos6.0 -configuration Release OBJROOT=\"build_intermediates\" SYMROOT=\"build_results\" IPHONEOS_DEPLOYMENT_TARGET=5.0 clean build";
		}
		else if (key.equals(SWARM_ANDROID_BUILD_CMD)) {
			return "ant";
		}	
		else if (key.equals(SWARM_GIT_CHECK_CMD)) {
			return "git rev-parse HEAD";
		}
		else if (key.equals(SWARM_CLEAN_REPO_CMD)) {
			return "rm -rf";
		}
		else if (key.equals(SWARM_NOTIFY_EMAIL_CMD)) {
			return "http://swarm.bytefly.com/email.php?name=639Building&bnum=4&build=639building-android-1.0.5.apk&log=buildlog-5.log&owner=bytefly&repo=639building-android&to=bob@bytefly.com&fname=AndroidBuild";
		}
		else if (key.equals(SWARM_ANDROID_CLEARN_CMD)) {
			return "rm -rf bin";
		}
		else if (key.equals(SWARM_ANDROID_FIND_MANIFEST)) {
			return "find . -name AndroidManifest.xml -print -quit";
		}
		else if (key.equals(SWARM_ANDROID_FIND_BUILDXML)) {
			return "find . -name build.xml -print -quit";
		}
		else if (key.equals(SWARM_ANDROID_APP_NAME)) {
			return "find . -name *.apk -print -quit";
		}
		else if (key.equals(SWARM_ANDROID_UPLOAD_APK)) {
			return "scp -P 22123 %s bpascazio@www.bytefly.com:builds/%s";
		}
		else if (key.equals(SWARM_UPLOAD_LOGFILE)) {
			return "scp -P 22123 %s bpascazio@www.bytefly.com:builds/%s";
		}
		else if (key.equals(SWARM_ANDROID_GENERATE_BUILDXML)) {
			return "tools/android update project -p .";
		}
		else if (key.equals(SWARM_ANDROID_SEND_EMAIL_APK)) {
			return "curl http://swarm.bytefly.com/email.php?name=%s&bnum=%d&build=%s&log=%s&owner=%s&repo=%s&to=%s&fname=Android%%20Build";
		}	
		else if (key.equals(SWARM_SEND_FAILURE_EMAIL)) {
			return "curl http://swarm.bytefly.com/femail.php?name=%s&bnum=%d&build=%s&log=buildlog-5.log&owner=%s&repo=%s&to=%s&fname=AndroidBuildFailure";
		}	
		else if (key.equals(SWARM_COLONY_AUTHENTICATION_V1)) {
			return "http://%s/colony?email=%s&password=%s";
		}	
		else if (key.equals(SWARM_COLONY_AUTHENTICATION_TOKEN)) {
			return "http://%s/tokens";
		}	
		else if (key.equals(SWARM_DUMP_XML)) {
			return "cat swarm.xml";
		} 
		else if (key.equals(SWARM_MAKE_PROJECT_DIR)) {
			return "mkdir";
		} 
		else if (key.equals(SWARM_MAKE_DOT_SWARM_DIR)) {
			return "mkdir .swarm";
		}
		Debug.Log(Debug.ERROR, "Undefined Config Value " + key);
		return "";
	}
}
