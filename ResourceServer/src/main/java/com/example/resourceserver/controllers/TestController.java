package com.example.resourceserver.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/me")
    @SecurityRequirement(name = "Bearer Authentication")
    public String test(Principal principal) {
        return principal != null ? principal.getName() : "";
    }
}
