package com.stocat.tradeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.stocat.common"
        }
)
public class PositionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PositionApiApplication.class, args);
	}

}
