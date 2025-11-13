package org.cloudlinux.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Entry point for the proof‑of‑concept application demonstrating CVE‑2016‑5007.
 *
 * This class uses the older configuration annotations since
 * {@code @SpringBootApplication} was introduced in later Spring Boot versions.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Example {

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }
}