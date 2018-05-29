package com.util;

public class Profile {

	public static final String ver="v0.1";

//	public static final String SYSTEMDIR = System.getProperty("user.dir");
	public static final String SYSTEMDIR = "/home/valhalla/Soft/apache-tomcat-9.0.8/webapps/ROOT/";

	public static final String JUDGER_WORKSPACE_BASE = "judger/run/";

	public static final String LOG_BASE = "judger/log/";
	public static final String COMPILER_LOG_PATH = SYSTEMDIR +LOG_BASE+"compile.log";
	public static final String JUDGER_RUN_LOG_PATH = SYSTEMDIR +LOG_BASE+"judger.log";
	public static final String SERVER_LOG_PATH = SYSTEMDIR +LOG_BASE+"judge_server.log";
	public static final int RUN_USER_UID = 0;
	public static final int RUN_GROUP_GID = 0;
	public static final int COMPILER_USER_UID = 0;
	public static final int COMPILER_GROUP_GID = 0;

	public static final String TEST_CASE_DIR = "judger/test_case/";

	public static final String SPJ_SRC_DIR = "/judger/spj";
	public static final String SPJ_EXE_DIR = "/judger/spj";
}
