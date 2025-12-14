package com.stocat.tradeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "com.stocat.common",
                "com.stocat.tradeapi"
        }
)
public class TradeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeApiApplication.class, args);
    }

}
