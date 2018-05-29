package main;

import com.bind.Compiler;
import com.bind.Judge_Do;
import com.rabbitmq.client.*;
import com.util.Profile;
import org.json.JSONArray;
import org.json.JSONObject;
import com.util.Send;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

public class Listen {
	public final static String QUEUE_QUE="rabbitMQ_test2";
	public final static String QUEUE_REC="rabbitMQ_test3";
	public final static String QUEUE_HOST="localhost";
	public final static String QUEUE_USER="guest";
	public final static String QUEUE_PWD="qweasd";
	public static void main(String[] args) throws java.io.IOException, java.lang.InterruptedException {
		get();
	}
	public static void get() throws java.io.IOException, java.lang.InterruptedException {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setUsername("guest");
			factory.setPassword("qweasd");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(QUEUE_QUE, false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
					try {
						JSONArray result=todo(consumerTag,envelope,properties,body);
						Send.send(Send.QUEUE_REC,result.toString());
					} catch (Exception e){
						System.out.println(e);
					}
				}
			};
			channel.basicConsume(QUEUE_QUE, true, consumer);
		} catch (Exception e){
			System.out.println(e);
		}
	}

	private static JSONArray todo(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws Exception{
		String message = new String(body, "UTF-8");
		System.out.println(" [x] Received '" + message + "'");
		JSONObject data = new JSONObject(message);

		if (data.getBoolean("isSPJ")==false){
			JSONObject json=data.getJSONObject("language_config");

			JSONObject compile_config=json.getJSONObject("compile");
			JSONObject run_config=json.getJSONObject("run");
			UUID uuid = UUID.randomUUID();
			String str = uuid.toString();

			String submission_id=str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
			String submission_dir="/home/valhalla/Soft/apache-tomcat-9.0.8/webapps/ROOT/"+Profile.JUDGER_WORKSPACE_BASE+submission_id;

			String src=data.getString("src");
			String src_path=submission_dir+"/"+compile_config.getString("src_name");
			String exe_path=Compiler.compileCAndCPP(src_path,null);

			if(compile_config!=null){
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

			int max_cpu_time=data.getInt("max_cpu_time");
			long max_memory=data.getLong("max_memory");
			int test_case_id=data.getInt("test_case_id");
			boolean output=data.getBoolean("output");

			Judge_Do judge_client=new Judge_Do(run_config,exe_path,max_cpu_time,max_memory,test_case_id,submission_dir,null,null,output);
			JSONArray r = judge_client.run();
			return r;

		}
		return null;
	}
}
