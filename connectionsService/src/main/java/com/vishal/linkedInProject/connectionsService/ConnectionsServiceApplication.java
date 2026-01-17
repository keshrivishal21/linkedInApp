package com.vishal.linkedInProject.connectionsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class ConnectionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectionsServiceApplication.class, args);
	}

}
