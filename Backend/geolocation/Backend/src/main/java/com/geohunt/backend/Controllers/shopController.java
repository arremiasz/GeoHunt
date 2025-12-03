package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.Services.ShopService;
import com.geohunt.backend.database.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
public class shopController {
    @Autowired
    ShopService shopService;

    @GetMapping("/getItem")
    public ResponseEntity<Shop> getItem(@RequestParam String name) {
        return shopService.getItem(name);
    }

    @DeleteMapping("/deleteItem")
    public ResponseEntity<String> deleteItem(@RequestParam String name) {
        return shopService.deleteItem(name);
    }


}
