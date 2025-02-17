package com.example.pilot_project;

import com.example.pilot_project.config.BasicIntegrationConfig;
import java.util.Scanner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

@SpringBootApplication
public class PilotProjectApplication {

	public static void main(String[] args) {
//		SpringApplication.run(PilotProjectApplication.class, args);
		AbstractApplicationContext context
				= new AnnotationConfigApplicationContext(BasicIntegrationConfig.class);
		context.registerShutdownHook();

		Scanner scanner = new Scanner(System.in);
		System.out.print("Please enter q and press <enter> to exit the program: ");

		while (true) {
			String input = scanner.nextLine(); 
			if ("q".equals(input.trim())) {
				break;
			}
		}
		System.exit(0);
	}

}
