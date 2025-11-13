package org.cloudlinux.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple controller exposing a public and an admin endpoint.  The admin
 * endpoint is intended to be protected by URL‑based security.  When
 * exploited via CVE‑2016‑5007, unauthenticated users can access the
 * admin endpoint by inserting whitespace into the path.
 */
@RestController
public class HelloController {

    @RequestMapping(value = "/public", method = RequestMethod.GET)
    public String publicEndpoint() {
        return "This is a public endpoint";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminEndpoint() {
        return "This is the admin endpoint";
    }
}