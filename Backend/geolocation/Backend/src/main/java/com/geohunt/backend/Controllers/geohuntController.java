package com.geohunt.backend.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class geohuntController {
    @GetMapping("/geohunt/getLocation")
    public String getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        return "No";
    }
}
