package com.herry.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class TaskApplication {

	public static void main(String[] args) throws Exception {
		/*Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://127.0.0.1:3306/task_demo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
				"root",
				"heyan123456"
		);*/
		SpringApplication.run(TaskApplication.class, args);
	}


}
