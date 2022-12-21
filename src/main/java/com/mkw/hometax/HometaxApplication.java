package com.mkw.hometax;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class HometaxApplication {

	public static void main(String[] args) {
		log.debug("log test >>> SUCCESS");
		SpringApplication.run(HometaxApplication.class, args);
	}
}
