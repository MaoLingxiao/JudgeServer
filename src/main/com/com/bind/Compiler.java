package com.bind;




import com.bean.Config;
import com.bean.Result;
import com.util.Profile;
import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {
	static String systemdir = System.getProperty("user.dir");

	public static String compile(JSONObject compile_config, String src_path, String output_dir){
		Bind bind = new Bind();
		String command = compile_config.getString("compile_command");
		String exe_path=null;
		try {
			exe_path = compileCAndCPP(src_path,null);
		} catch (Exception e){
			System.out.println(e);
		}
		System.out.println("exp:"+exe_path);
		//command = "";
		String compiler_out = systemdir+output_dir+"compiler.out";
		String[] _command = command.split(" ");
		Config config = new Config();
		config.max_cpu_time=compile_config.getInt("max_cpu_time");
		config.max_real_time=compile_config.getInt("max_real_time");
		config.max_memory=compile_config.getInt("max_memory");
		config.max_output_size=1024*1024;
		config.max_process_number = Integer.MAX_VALUE;
		config.exe_path=_command[0];
		config.input_path=src_path;
		config.output_path=compiler_out;
		config.error_path=compiler_out;
		config.args=new String[10];
		for (int i=1;i<_command.length;i++){
			config.args[i-1]=_command[i];
			System.out.println(config.args[i-1]=_command[i]);
		}
		config.env=new String[10];
		config.env[0]="PATH="+systemdir;
		config.log_path= Profile.COMPILER_LOG_PATH;
		config.seccomp_rule_name="";
		config.uid=Profile.COMPILER_USER_UID;
		config.gid=Profile.COMPILER_GROUP_GID;


		System.out.println(config.max_cpu_time);
		System.out.println(config.max_real_time);
		System.out.println(config.max_memory);
		System.out.println(config.max_process_number);
		System.out.println(config.max_output_size);
		System.out.println(config.exe_path);
		System.out.println(config.input_path);
		System.out.println(config.output_path);
		System.out.println(config.error_path);
		System.out.println(config.args[0]);
		System.out.println(config.env[0]);
		System.out.println(config.log_path);
		System.out.println(config.seccomp_rule_name);
		System.out.println(config.uid);
		System.out.println(config.gid);

		Result result = bind.c_coreStart(config);
		File file = new File(compiler_out);
		if (result.result!=0){
			if(file.exists()){
				try {
					FileReader fileReader = new FileReader(compiler_out);
					StringBuilder stringBuilder = new StringBuilder();
					int tmp;
					while((tmp=fileReader.read())!=-1 ){
						stringBuilder.append((char)tmp);
					}
					fileReader.close();
					if (stringBuilder.toString()!=null||!stringBuilder.toString().isEmpty())
						System.out.println(stringBuilder.toString());
					else
						System.out.println("Compiler runtime error");
				}  catch (Exception e){
					System.out.println(e);
				}

			}
			return null;
		} else {
			file.delete();
			return exe_path;
		}
	}

	public static String compileCAndCPP(String src_name,String extra_flags) throws Exception{
		try {
			String name = src_name.substring(0,src_name .lastIndexOf("."));
			String cmd = "gcc "+src_name+" -o "+name;
			System.out.println(cmd);
			final Process process = Runtime.getRuntime().exec(cmd); // 执行编译指令
			process.waitFor();
			return name;
		} catch (IOException e) {
			System.out.println("compile:"+src_name+" can't run");
			e.printStackTrace();
		}
		return null;
	}

}
