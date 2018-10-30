package com.toennies.ci1429.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.toennies" })
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		logApplicationStartup();
	}

	/**
	 * Is needed because of CI1429APP-63
	 *
	 * Through this it's clear that startup is finshed. Information is displayed
	 * in windows/linux bash. This information is printed via syso, because
	 * console logger is disabled in dev/prod profile.
	 */
	private static void logApplicationStartup() {
		System.out.println("HW20 startup finished.");
	}
}
