package com.amdose.server.resource.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Amdose Team
 */
@RestController
public class TestController {

    @GetMapping("/test/worked")
    public String test() {
        return "It Worked!";
    }
}
