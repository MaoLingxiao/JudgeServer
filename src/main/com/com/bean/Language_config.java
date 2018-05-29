package com.bean;

import org.json.JSONObject;

public class Language_config {
	public static final String default_env = "[\"LANG=en_US.UTF-8\", \"LANGUAGE=en_US:en\", \"LC_ALL=en_US.UTF-8\"]";
	public static final String c_l_c = "{\n" +
			"\t\"compile\": {\n" +
			"\t\t\"src_name\": \"main.c\",\n" +
			"\t\t\"exe_name\": \"main\",\n" +
			"\t\t\"max_cpu_time\": 3000,\n" +
			"\t\t\"max_real_time\": 5000,\n" +
			"\t\t\"max_memory\": "+128*1024*1024+",\n" +
			"\t\t\"compile_command\": \"/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c99 {src_path} -lm -o {exe_path}\"\n" +
			"\t},\n" +
			"\t\"run\": {\n" +
			"\t\t\"command\": [\"exe_path\"],\n" +
			"\t\t\"seccomp_rule\": \"c_cpp\",\n" +
			"\t\t\"env\": "+default_env+"\n" +
			"\t}\n" +
			"}";
	public static final String c_l_s_compile = "{\n" +
			"\t\"src_name\": \"spj-{spj_version}.c\",\n" +
			"\t\"exe_name\": \"spj-{spj_version}\",\n" +
			"\t\"max_cpu_time\": 3000,\n" +
			"\t\"max_real_time\": 5000,\n" +
			"\t\"max_memory\": "+1024 * 1024 * 1024+",\n" +
			"\t\"compile_command\": \"/usr/bin/gcc -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c99 {src_path} -lm -o {exe_path}\"\n" +
			"}";
	public static final String c_l_s_config = "{\n" +
			"    \"exe_name\": \"spj-{spj_version}\",\n" +
			"    \"command\": \"{exe_path} {in_file_path} {user_out_file_path}\",\n" +
			"    \"seccomp_rule\": \"c_cpp\"\n" +
			"}";
	public static final String cpp_l_c = "{\n" +
			"\t\"compile\": {\n" +
			"\t\t\"src_name\": \"main.cpp\",\n" +
			"\t\t\"exe_name\": \"main\",\n" +
			"\t\t\"max_cpu_time\": 3000,\n" +
			"\t\t\"max_real_time\": 5000,\n" +
			"\t\t\"max_memory\": "+128*1024*1024+",\n" +
			"\t\t\"compile_command\": \"/usr/bin/g++ -DONLINE_JUDGE -O2 -w -fmax-errors=3 -std=c++11 {src_path} -lm -o {exe_path}\"\n" +
			"\t},\n" +
			"\t\"run\": {\n" +
			"\t\t\"command\": \"{exe_path}\",\n" +
			"\t\t\"seccomp_rule\": \"c_cpp\",\n" +
			"\t\t\"env\": "+default_env+"\n" +
			"\t}\n" +
			"}";
	public static final String java_l_c = "{\n" +
			"\t\"name\": \"java\",\n" +
			"\t\"compile\": {\n" +
			"\t\t\"src_name\": \"Main.java\",\n" +
			"\t\t\"exe_name\": \"Main\",\n" +
			"\t\t\"max_cpu_time\": 3000,\n" +
			"\t\t\"max_real_time\": 5000,\n" +
			"\t\t\"max_memory\": -1,\n" +
			"\t\t\"compile_command\": \"/usr/bin/javac {src_path} -d {exe_dir} -encoding UTF8\"\n" +
			"\t},\n" +
			"\t\"run\": {\n" +
			"\t\t\"command\": \"/usr/bin/java -cp {exe_dir} -XX:MaxRAM={max_memory}k -Djava.security.manager -Dfile.encoding=UTF-8 -Djava.security.policy==/etc/java_policy -Djava.awt.headless=true Main\",\n" +
			"\t\t\"seccomp_rule\": null,\n" +
			"\t\t\"env\": "+default_env+",\n" +
			"\t\t\"memory_limit_check_only\": 1\n" +
			"\t}\n" +
			"}";
	public static final String py2_l_c = "{\n" +
			"\t\"compile\": {\n" +
			"\t\t\"src_name\": \"solution.py\",\n" +
			"\t\t\"exe_name\": \"solution.pyc\",\n" +
			"\t\t\"max_cpu_time\": 3000,\n" +
			"\t\t\"max_real_time\": 5000,\n" +
			"\t\t\"max_memory\": "+128*1024*1024+",\n" +
			"\t\t\"compile_command\": \"/usr/bin/python -m py_compile {src_path}\"\n" +
			"\t},\n" +
			"\t\"run\": {\n" +
			"\t\t\"command\": \"/usr/bin/python {exe_path}\",\n" +
			"\t\t\"seccomp_rule\": \"general\",\n" +
			"\t\t\"env\": "+default_env+"\n" +
			"\t}\n" +
			"}";
	public static final String py3_l_c = "{\n" +
			"\t\"compile\": {\n" +
			"\t\t\"src_name\": \"solution.py\",\n" +
			"\t\t\"exe_name\": \"__pycache__/solution.cpython-35.pyc\",\n" +
			"\t\t\"max_cpu_time\": 3000,\n" +
			"\t\t\"max_real_time\": 5000,\n" +
			"\t\t\"max_memory\": "+128*1024*1024+",\n" +
			"\t\t\"compile_command\": \"/usr/bin/python3 -m py_compile {src_path}\"\n" +
			"\t},\n" +
			"\t\"run\": {\n" +
			"\t\t\"command\": \"/usr/bin/python3 {exe_path}\",\n" +
			"\t\t\"seccomp_rule\": \"general\",\n" +
			"\t\t\"env\": [\"PYTHONIOENCODING=UTF-8\""+", "+default_env.substring(1,default_env.length()-1)+"]"+"\n" +
			"\t}\n" +
			"}";

	public static final JSONObject c_lang_config = new JSONObject(c_l_c);
	public static final JSONObject c_lang_spj_compile = new JSONObject(c_l_s_compile);
	public static final JSONObject c_lang_spj_config = new JSONObject(c_l_s_config);
	public static final JSONObject cpp_lang_config = new JSONObject(cpp_l_c);
	public static final JSONObject java_lang_config = new JSONObject(java_l_c);
	public static final JSONObject py2_lang_config = new JSONObject(py2_l_c);
	public static final JSONObject py3_lang_config = new JSONObject(py3_l_c);

}
