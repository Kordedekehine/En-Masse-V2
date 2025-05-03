package com.enmasse.Notification_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class NotificationServiceApplication {

	public static void main(String[] args)  {

		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
