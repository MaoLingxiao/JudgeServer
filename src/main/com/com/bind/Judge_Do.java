package com.bind;

import com.bean.Config;
import com.bean.Result;
import com.util.Profile;
import com.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

public class Judge_Do {
	public static final int SPJ_WA=1;
	public static final int SPJ_AC=0;
	public static final int SPJ_ERROR=-1;

	private JSONObject _run_config;
	String _exe_path;
	int _max_cpu_time;
	long _max_memory;
	long _max_real_time;
	int _test_case_id;
	String _test_case_dir;
	String _submission_dir;
	JSONObject _test_case_info;
	String _spj_version;
	String _spj_config;
	boolean _output;
	String _spj_exe;


	String user_output_file;
	JSONObject _run_result;
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	public Judge_Do(JSONObject run_config, String exe_path, int max_cpu_time, long max_memory, int test_case_id,
					String submission_dir, String spj_version, String spj_config, boolean output){
		System.out.println("JUDGE_CLIENT_init");
		this._run_config=run_config;
		this._exe_path=exe_path;
		this._max_cpu_time=max_cpu_time;
		this._max_memory=max_memory;
		this._max_real_time=this._max_cpu_time*3;
		this._test_case_id=test_case_id;
		this._test_case_dir = Profile.SYSTEMDIR +Profile.TEST_CASE_DIR+ "normal/";
		this._submission_dir=submission_dir;
		//pool
		this._test_case_info=_load_test_case_info();
		this._spj_version=spj_version;
		this._spj_config=spj_config;
		this._output=output;

		System.out.println(_run_config);
		System.out.println(_exe_path);
		System.out.println(_max_cpu_time);
		System.out.println(_max_memory);
		System.out.println(_max_real_time);
		System.out.println(_test_case_id);
		System.out.println(_test_case_dir);
		System.out.println(_submission_dir);
		System.out.println(_test_case_info);
		System.out.println(_spj_version);
		System.out.println(_spj_config);
		System.out.println(_output);

		if(this._spj_version!=null&&this._spj_config!=null){
			//adapt
			this._spj_exe=Profile.SYSTEMDIR + Profile.SPJ_EXE_DIR+this._spj_config;
			File file = new File(this._spj_exe);
			if(!file.exists())
				System.out.println("spj exe not found");
		}
		System.out.println("---------------------------------------");
	}

	public JSONObject _load_test_case_info(String test_case_file_id){
		//return this._test_case_info["test_cases"][test_case_file_id]
		File file = new File(Profile.SYSTEMDIR +_test_case_dir+"info");
		try {
			FileReader fileReader = new FileReader(file);
			StringBuilder stringBuilder = new StringBuilder();
			int tmp;
			while((tmp=fileReader.read())!=-1 ){
				stringBuilder.append((char)tmp);
			}
			fileReader.close();
			JSONObject jsonObject = new JSONObject(stringBuilder.toString());
			return jsonObject;
		} catch (Exception e){
			System.out.println(e);
		}
		return null;
	}

	public JSONObject _get_test_case_file_info(int test_case_file_id){
		return _test_case_info.getJSONObject("test_cases").getJSONObject(String.valueOf(test_case_file_id));
	}

	public String _compare_output(int test_case_file_id){
		user_output_file = Profile.SYSTEMDIR +this._submission_dir+test_case_file_id+".out";
		try {
			FileReader fileReader = new FileReader(user_output_file);
			StringBuilder stringBuilder = new StringBuilder();
			int tmp;
			while((tmp=fileReader.read())!=-1 ){
				stringBuilder.append((char)tmp);
			}
			fileReader.close();
			MessageDigest md5=MessageDigest.getInstance("MD5");
			BASE64Encoder base64en = new BASE64Encoder();

			String output_md5 = base64en.encode(md5.digest(stringBuilder.toString().getBytes("utf-8")));
			//result = output_md5 == self._get_test_case_file_info(test_case_file_id)["stripped_output_md5"]
			return output_md5;
		} catch (Exception e){
			System.out.println(e);
		}
		return null;
	}

	public int _spj(String in_file_path,String user_out_file_path){
		//command = this._spj_config["command"].format(exe_path=self._spj_exe, in_file_path=in_file_path, user_out_file_path=user_out_file_path).split(" ")
		String command=this._spj_config;
		String seccomp_rule_name=this._spj_config;
		Bind bind = new Bind();
		Config config = new Config();
		config.max_cpu_time=this._max_cpu_time*3;
		config.max_real_time=this._max_cpu_time*9;
		config.max_memory=this._max_memory*3;
		config.max_output_size=1024 * 1024 * 1024;
		config.max_process_number=Integer.MAX_VALUE;
		//command[0];
		config.exe_path=command;
		config.input_path=in_file_path;
		config.output_path="/tmp/spj.out";
		config.error_path="/tmp/spj.out";
		/*for (int i=1;i<command.length;i++)
			config.args[i-1]=command[i];*/
		config.args[0]=command;
		config.env[0]="PATH="+"";
		config.log_path= Profile.JUDGER_RUN_LOG_PATH;
		config.seccomp_rule_name=seccomp_rule_name;
		config.uid=Profile.RUN_USER_UID;
		config.gid=Profile.RUN_GROUP_GID;
		Result result=bind.c_coreStart(config);
		if(result.result==0||(result.result==4&&(result.exit_code==SPJ_WA||result.exit_code==SPJ_ERROR)&&result.signal==0)){
			return result.exit_code;
		}
		else return SPJ_ERROR;
	}

	public JSONObject _judge_one(int test_case_file_id){
		System.out.println("____run_one");
		JSONObject test_case_info=_get_test_case_file_info(test_case_file_id);
		System.out.println(test_case_info.toString());
		String in_file=this._test_case_dir+test_case_info.getString("input_name");
		System.out.println(in_file);
		String user_output_file=this._submission_dir+"/"+test_case_file_id+".out";
		System.out.println(user_output_file);


		//.format(exe_path=self._exe_path, exe_dir=os.path.dirname(self._exe_path),
		//		max_memory=int(self._max_memory / 1024)).split(" ")

		JSONArray command=_run_config.getJSONArray("command");
		System.out.println(command);
		//["PATH=" + os.environ.get("PATH", "")] + self._run_config.get("env", [])
		String[] env={"PATH="+Profile.SYSTEMDIR};
		System.out.println(env[0]);

		Config config = new Config();
		config.max_cpu_time=this._max_cpu_time;
		config.max_real_time=this._max_cpu_time;
		config.max_memory=this._max_memory;
		config.max_output_size=1024*1024*2;
		config.max_process_number=10;
		System.out.println("max_cpu_time:"+config.max_cpu_time);
		System.out.println("max_real_time:"+config.max_real_time);
		System.out.println("max_memory:"+config.max_memory);
		System.out.println("max_output_size:"+config.max_output_size);
		System.out.println("max_process_number:"+config.max_process_number);
		//command[0];
//		config.exe_path=command.getString(0);
		config.exe_path=_exe_path;
		config.input_path=in_file;
		config.output_path=user_output_file;
		config.error_path=user_output_file;
		System.out.println("exe_path:"+config.exe_path);
		System.out.println("input_path:"+config.input_path);
		System.out.println("output_path:"+config.output_path);
		System.out.println("error_path:"+config.error_path);
		System.out.println(command.length());
//		if(1<command.length()){
//			for (int i=0;i<command.length();i++)
//				config.args[i-1]=command.getString(i);
//		} else {
//			config.args[0]="";
//		}
		config.args=new String[command.length()];
		config.args[0]=_exe_path;
		System.out.println("args:"+config.args[0]);
		config.env=new String[3];
		config.env=env;
		System.out.println("env:"+config.env[0]);
		config.log_path= Profile.JUDGER_RUN_LOG_PATH;
		System.out.println("log_path:"+config.log_path);
//		config.seccomp_rule_name=_run_config.getString("seccomp_rule");
		config.setSeccomp_rule_name("c_cpp");
		System.out.println("seccomp_rule_name:"+config.seccomp_rule_name);
		config.uid=Profile.RUN_USER_UID;
		config.gid=Profile.RUN_GROUP_GID;
		System.out.println("uid:"+config.uid);
		System.out.println("gid:"+config.gid);
		//memory_limit_check_only=self._run_config.get("memory_limit_check_only", 0
		Bind bind=new Bind();
		Result result=bind.c_coreStart(config);
		if (result.signal!=10){
			_run_result = new JSONObject();
			_run_result.put("cpu_time",result.cpu_time);
			_run_result.put("real_time",result.real_time);
			_run_result.put("memory",result.memory);
			_run_result.put("result",result.result);
			_run_result.put("signal",result.signal);
			_run_result.put("exit_code",result.exit_code);
			_run_result.put("error",result.error);
			String output_md5 = "";
			try {
				FileReader fileReader = new FileReader(config.output_path);
				StringBuilder stringBuilder = new StringBuilder();
				int tmp;
				while((tmp=fileReader.read())!=-1 ){
					stringBuilder.append((char)tmp);
				}
				output_md5 = Util.getMD5(stringBuilder.toString());
				fileReader.close();
			} catch (Exception e){
				System.out.println(e);
			}
			_run_result.put("output_md5",output_md5);
			_run_result.put("test_case",test_case_file_id);
		} else {
			_run_result = new JSONObject();
			_run_result.put("err","CompileError");
			_run_result.put("data","error resson");
		}
		return _run_result;
	}

	public JSONObject _run(int test_case_file_id){
		return _judge_one(test_case_file_id);
	}

	public JSONArray run(){
		String[] tmp_result={};
		String[] result={};
		Map<String,Object> map=_test_case_info.toMap();
		ArrayList<Future<JSONObject>> results = new ArrayList<Future<JSONObject>>();
		JSONArray r = new JSONArray();
		//final Map.Entry<String, Object> entry : map.entrySet()
		for (int i=0;i<1;i++) {
			Callable<JSONObject> newCallable = new Callable<JSONObject>() {
				JSONObject tmp;
				@Override
				public JSONObject call() throws Exception {
//					!!! wrong id from ENTRY
					int test_case_file_id=1;
					tmp=_run(test_case_file_id);
					return tmp;
				}
			};
//			Runnable newRunnable = new Runnable() {
//				@Override
//				public void run() {
//					int test_case_file_id;
//					try {
//						test_case_file_id=1;
//						JSONObject tmp=_run(test_case_file_id);
//						r.put(tmp);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//				}
//			};
			results.add(executor.submit(newCallable));
		}
		for (Future<JSONObject> future:results)
			try {
				System.out.println("rrr"+future.get().toString());
				r.put(future.get());
			}catch (Exception e){
				System.out.println(e);
			}
		return r;
	}

	public void getstate(){
		/*
		self_dict = self.__dict__.copy()
        del self_dict["_pool"]
        return self_dict
		* */
	}

	private JSONObject _load_test_case_info(){
		JSONObject jsonObject = null;
		try {
			FileReader fileReader = new FileReader(this._test_case_dir+"info");
			StringBuilder stringBuilder = new StringBuilder();
			int tmp;
			while((tmp=fileReader.read())!=-1 ){
				stringBuilder.append((char)tmp);
			}
			jsonObject = new JSONObject(stringBuilder.toString());
			fileReader.close();
		} catch (Exception e){
			System.out.println(e);
		}
		return jsonObject;
	}
}
