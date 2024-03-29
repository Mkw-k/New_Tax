package com.mkw.hometax.index;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
public class WebRestController {
    @Autowired
    private Environment env;

    @GetMapping("/api/profile")
    public String getProfile(){
        return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("fail");
    }
}
