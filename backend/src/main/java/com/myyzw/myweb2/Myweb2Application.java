/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Myweb2Application {

    public static void main(String[] args) {
        SpringApplication.run(Myweb2Application.class, args);
    }

}
