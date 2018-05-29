package control;

import com.bind.Compiler;
import com.bind.Judge_Do;
import com.util.Profile;
import com.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@WebServlet("/judge")
public class judge extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json;charset=utf-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("X-Judge-Server-Token",Util.getSHA256StrJava("token"));

		//from mq string -> json;
//		String data = (String)req.getAttribute("data");
		JSONObject json = (JSONObject)req.getAttribute("language_config");
		System.out.println(json);
//		JSONObject json=new JSONObject(data);

		JSONObject compile_config=json.getJSONObject("compile");
		JSONObject run_config=json.getJSONObject("run");
		UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 8 4 4 4 12
		String submission_id=str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);

		String spj_version=(String)req.getAttribute("spj_version");
		String spj_config_data=(String)req.getAttribute("spj_config");
		String src=(String)req.getAttribute("src");
		String exe_path=(String)req.getAttribute("exe_path");
		String output_dir=(String)req.getAttribute("output_dir");

		if(spj_version!=null&&spj_config_data!=null){
			JSONObject spj_config=new JSONObject("spj_config_data");
			//spj
			String spj_exe_path =Util.systemdir+Profile.SPJ_EXE_DIR+spj_config.getString("exe_name");
			File exe=new File(spj_exe_path);
			if(!exe.exists()){
				System.out.println("%s does not exists, spj src will be recompiled");
				//compile_spj(spj_version=spj_version, src=spj_src, spj_compile_config=spj_compile_config)
			}
		}

//		InitSubmissionEnv initSubmissionEnv=new InitSubmissionEnv(Profile.JUDGER_WORKSPACE_BASE,submission_id);
//		String submission_dir=initSubmissionEnv.path;
		String submission_dir=req.getSession().getServletContext().getRealPath("")+Profile.JUDGER_WORKSPACE_BASE+submission_id;
		if(compile_config!=null){
			String src_path=submission_dir+"/"+compile_config.getString("src_name");
			File dir = new File(submission_dir);
			if(!dir.exists())
				dir.mkdirs();
			System.out.println(src_path);
			FileWriter fileWriter = new FileWriter(src_path);
			fileWriter.write(src);
			fileWriter.close();
			try {
				exe_path=Compiler.compileCAndCPP(src_path,null);
			} catch (Exception e){
				System.out.println(e);
			}
		} else {
			exe_path=Profile.SYSTEMDIR +run_config.getString("exe_name");
			File file=new File(exe_path);
			FileWriter fileWriter=new FileWriter(file);
			fileWriter.write(src);
			fileWriter.close();
		}

		int max_cpu_time=(int)req.getAttribute("max_cpu_time");
		long max_memory=(long)req.getAttribute("max_memory");
		int test_case_id=(int) req.getAttribute("test_case_id");
		boolean output=(boolean)req.getAttribute("output");

		System.out.println("---------------------------------------");
		System.out.println("JUDGE");
		System.out.println("run_con:"+run_config);
		System.out.println("exe_path:"+exe_path);
		System.out.println("max_cpu_time:"+max_cpu_time);
		System.out.println("max_memory:"+max_memory);
		System.out.println("test_case_id:"+test_case_id);
		System.out.println("submission_dir:"+submission_dir);
		System.out.println("spj_version:"+spj_version);
		System.out.println("spj_config_data:"+spj_config_data);
		System.out.println("output:"+output);
		System.out.println("---------------------------------------");

		Judge_Do judge_client=new Judge_Do(run_config,exe_path,max_cpu_time,max_memory,test_case_id,submission_dir,spj_version,spj_config_data,output);
//		Result run_result=judge_client.run();
		JSONArray r = judge_client.run();

		resp.getWriter().write(r.toString());
	}

	private class InitSubmissionEnv{
		public String path;
		public InitSubmissionEnv(String judger_workspace,String submission_id){
			this.path=Profile.SYSTEMDIR +judger_workspace+submission_id;
		}
		public void getpath(){
			File dir=new File(path);
			if(!dir.exists()){
				dir.mkdir();
			}
		}
		public boolean exit(){
			if(Util.deleteFolder(path))
				return true;
			return false;
		}
	}
}
