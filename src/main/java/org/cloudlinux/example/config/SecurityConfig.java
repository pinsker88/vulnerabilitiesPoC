package org.cloudlinux.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

/**
 * Custom security configuration that sets up in‑memory users and URL‑based
 * access control.  The rules intentionally permit all requests except
 * those matching the admin path.  Because of CVE‑2016‑5007, placing a
 * whitespace character before or after the path segment will cause the
 * security filter to ignore the rule and allow the request through.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Configure two users in memory: one normal user and one admin user.
        auth.inMemoryAuthentication()
            .passwordEncoder(NoOpPasswordEncoder.getInstance())
            .withUser("user").password("password").roles("USER")
            .and()
            .withUser("admin").password("password").roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Protect the admin path.  Requests that do not match
                // this pattern will fall through to the permitAll rule below.
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
            // Use HTTP Basic authentication for simplicity.  Without credentials
            // the server will challenge the client with a 401.
            .httpBasic()
                .and()
            // Disable CSRF to simplify testing with curl/postman.  In a real
            // application you should leave CSRF enabled.
            .csrf().disable();
    }
}