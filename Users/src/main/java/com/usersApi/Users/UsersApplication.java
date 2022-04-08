package com.usersApi.Users;

import com.usersApi.Users.Configuration.BigQueryAppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
public class UsersApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(UsersApplication.class, args);

	}

}
