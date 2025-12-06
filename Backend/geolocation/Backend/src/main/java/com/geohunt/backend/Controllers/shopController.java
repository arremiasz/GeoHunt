package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.ShopService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
import com.geohunt.backend.database.ShopRepository;
import com.geohunt.backend.util.PaymentDTO;
import com.geohunt.backend.util.SHOP_ITEM_TYPE;
import com.geohunt.backend.database.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/shop")
public class shopController {
    @Autowired
    ShopService shopService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/getItem")
    public ResponseEntity<Shop> getItem(@RequestParam String name) {
        return shopService.getItem(name);
    }

    @GetMapping("/getItemId")
    public ResponseEntity<Shop> getItemId(@RequestParam long id) {
        return shopService.getItem(id);
    }

    @DeleteMapping("/deleteItem")
    public ResponseEntity<String> deleteItem(@RequestParam String name) {
        return shopService.deleteItem(name);
    }

    @GetMapping("/getType")
    public ResponseEntity<List<Shop>> getItemType(@RequestParam SHOP_ITEM_TYPE itemType) {
        return shopService.getOfType(itemType);
    }

    @PostMapping("/addItem")
    public ResponseEntity<Shop> addItem(@RequestBody Shop shop) {
        return shopService.addItem(shop);
    }

    @PostMapping("/purchase")
    public ResponseEntity<String> purchase(@RequestBody PaymentDTO paymentDTO) {
        return shopService.purchaseControl(paymentDTO);
    }
}
