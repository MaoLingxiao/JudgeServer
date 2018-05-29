package com.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {
	public final static String QUEUE_QUE="rabbitMQ_test2";
	public final static String QUEUE_REC="rabbitMQ_test3";
	public final static String QUEUE_HOST="localhost";
	public final static String QUEUE_USER="guest";
	public final static String QUEUE_PWD="qweasd";
	public static void send(String name,String msg) {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			factory.setUsername("guest");
			factory.setPassword("qweasd");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();
			channel.queueDeclare(name, false, false, false, null);
			channel.basicPublish("", name, null, msg.getBytes());
			System.out.println("[MQ]Sent to "+name+":'" + msg + "'");
			channel.close();
			connection.close();
		} catch (Exception e){
			System.out.println(e);
		}
	}
}
