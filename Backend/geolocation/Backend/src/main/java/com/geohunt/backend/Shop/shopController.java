package com.geohunt.backend.Shop;

import com.geohunt.backend.Shop.DTOs.PowerupShopDTO;
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
    @Autowired
    private TransactionService transactionService;

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

    @PostMapping("/addPowerup")
    public ResponseEntity<Shop> addPowerup(@RequestBody PowerupShopDTO powerupDTO) {
        return shopService.addPowerupItem(powerupDTO);
    }

    @PostMapping("/addItem")
    public ResponseEntity<Shop> addItem(@RequestBody Shop shop) {
        return shopService.addItem(shop);
    }

    @PutMapping("/equip")
    public ResponseEntity equip(@RequestParam String itemName, @RequestParam long uid){
        return shopService.equip(itemName, uid);
    }

    @PutMapping("/unequip")
    public ResponseEntity unequip(@RequestParam String itemName, @RequestParam long uid){
        return shopService.unequip(itemName, uid);
    }

    @GetMapping("/usePowerup")
    public ResponseEntity usePowerup(@RequestParam String itemName, @RequestParam long uid){
        return shopService.userPowerup(itemName, uid);
    }

    @DeleteMapping("/transactions")
    public void deleteTransaction(@RequestParam long tid){ //DEV ONLY
        transactionService.deleteTransaction(tid);
    }

    @GetMapping("/transactions/by-id")
    public ResponseEntity getTransaction(@RequestParam long tid){
        return transactionService.getTransactionById(tid);
    }

    @GetMapping("/transactions/by-user")
    public ResponseEntity getTransactions(@RequestParam long uid){
        return transactionService.getUsersTransactions(uid);
    }

    /**@PostMapping("/purchase")
    public ResponseEntity<String> purchase(@RequestBody PaymentDTO paymentDTO) {
        return shopService.purchaseControl(paymentDTO);
    }**/
}
