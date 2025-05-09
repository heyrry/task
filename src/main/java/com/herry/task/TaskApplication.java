package com.herry.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class TaskApplication {

	public static void main(String[] args) throws Exception {

		SpringApplication.run(TaskApplication.class, args);
	}


}
