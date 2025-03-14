package com.amdose.server.resource.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Amdose Team
 */
@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping("/worked")
    public String test() {
        return "It Worked!";
    }
}
