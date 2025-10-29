package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import com.geohunt.backend.database.Submissions;
import com.geohunt.backend.database.SubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class geohuntController {

    @GetMapping("/geohunt/getLocation")
    public String getLocation(@RequestParam double lat, @RequestParam double lng, @RequestParam double radius) {
        return "No";
    }

}
