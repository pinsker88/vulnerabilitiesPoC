package org.cloudlinux.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A minimal Spring Boot application using Spring Framework 5.2.0.RELEASE.
 *
 * <p>This application is identical to the CVE‑2021‑22096 PoC but is used to
 * illustrate CVE‑2021‑22060.  In Spring Framework 5.2.0, the
 * {@code ResourceHttpRequestHandler} logs the original request path when it
 * contains {@code "../"} after normalisation.  While CVE‑2021‑22096
 * highlights newline injection, CVE‑2021‑22060 shows that backspace and
 * ANSI escape sequences are also logged unsanitised【913263586918717†L135-L173】.</p>
 */
@SpringBootApplication
public class Example {
    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

    /**
     * A trivial endpoint to confirm the application is running.
     */
    @RestController
    static class HelloController {
        @GetMapping("/hello")
        public String hello() {
            return "Hello from CVE‑2021‑22060 PoC";
        }
    }
}