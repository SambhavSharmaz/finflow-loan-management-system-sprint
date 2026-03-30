package com.capgemini.authservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigDemoController {

    @Value("${finflow.message:No message found}")
    private String message;

    @GetMapping("/demo-config")
    public String getMessage() {
        return "Message from GitHub Config: " + message;
    }
}
