package io.segmentme.channelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "io.segmentme")
@EnableAsync
public class ChannelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChannelServiceApplication.class, args);
    }
}
