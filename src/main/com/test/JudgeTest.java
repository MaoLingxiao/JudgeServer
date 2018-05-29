package test;

import com.bean.Language_config;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/JudgeTest")
public class JudgeTest extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json;charset=utf-8");
		resp.setCharacterEncoding("UTF-8");
//		resp.setHeader("X-Judge-Server-Token",Util.getSHA256StrJava("token"));
		String src="#include <stdio.h>\n" +
				"#include <string.h>\n" +
				"#include <stdlib.h>\n" +
				"ints arr[1024000];\n" +
				"int main()\n" +
				"{\n" +
//				"    int a;\n" +
//				"    int b;\n" +
//				"int arr[102400000];\n" +
				"memset(arr, 1, sizeof(arr));\n"+
//				"    scanf(\"%d\", &a);\n" +
//				"    scanf(\"%d\", &b);\n" +
//				"    printf(\"%d\",a+b);\n" +

				"    return 0;\n" +
				"}";
		req.setAttribute("src",src);
		JSONObject language_config=new JSONObject(Language_config.c_l_c);
		req.setAttribute("language_config",language_config);
		int max_cpu_time=1000;
		req.setAttribute("max_cpu_time",max_cpu_time);
		long max_memory=1024*1024*64;
		req.setAttribute("max_memory",max_memory);
		int test_case_id=1;
		req.setAttribute("test_case_id",test_case_id);
		boolean output=true;
		req.setAttribute("output",output);

//		JSONObject data=new JSONObject();
//		data.put("src",src);
//		data.put("language_config",language_config);
//		data.put("max_cpu_time",max_cpu_time);
//		data.put("max_memory",max_memory);
//		data.put("test_case_id",test_case_id);
//		data.put("output",true);

//		req.setAttribute("data",data);
		// json -> stirng to mq
		req.getRequestDispatcher("/judge").forward(req,resp);
	}
}
