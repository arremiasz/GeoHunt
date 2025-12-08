package com.geohunt.backend.Shop;

import com.geohunt.backend.database.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
