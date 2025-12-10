package com.geohunt.backend.Shop;

import com.geohunt.backend.Shop.DTOs.PowerupResponseDTO;
import com.geohunt.backend.Shop.DTOs.PowerupShopDTO;
import com.geohunt.backend.Shop.DTOs.ShopResponseDTO;
import com.geohunt.backend.Shop.DTOs.TransactionDTO;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
//import com.stripe.exception.StripeException;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
import com.geohunt.backend.powerup.Powerup;
import com.geohunt.backend.powerup.PowerupRepository;
import com.geohunt.backend.powerup.PowerupService;
import com.geohunt.backend.rewards.RewardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PowerupRepository powerupRepository;
    @Autowired
    private UserInventoryRepository userInventoryRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PowerupService powerupService;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private RewardRepository rewardRepository;

    public ResponseEntity<String> deleteItem(String name) {
        Optional<Shop> s = shopRepository.findByName(name);
        if (s.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }
        shopRepository.delete(s.get());
        userInventoryRepository.deleteAllByShopItem(s.get());
        return ResponseEntity.status(HttpStatus.OK).body("Shop Item deleted.");
    }

    public ResponseEntity<Map<SHOP_ITEM_TYPE, List<Shop>>> getAllItems() {
        Map<SHOP_ITEM_TYPE, List<Shop>> map = new HashMap<>();
        ResponseEntity a = getOfType(SHOP_ITEM_TYPE.DECORATION);
        if(a.getStatusCode() == HttpStatus.OK) {
            List<Shop> DECORATION = (List) a.getBody();
            map.put(SHOP_ITEM_TYPE.DECORATION, DECORATION);
        }
        ResponseEntity b = getOfType(SHOP_ITEM_TYPE.PROFILE_CUSTOMIZATION);
        if(b.getStatusCode() == HttpStatus.OK) {
            List<Shop> PROFILE_CUSTOMIZATION = (List) b.getBody();
            map.put(SHOP_ITEM_TYPE.PROFILE_CUSTOMIZATION, PROFILE_CUSTOMIZATION);
        }
        ResponseEntity c = getOfType(SHOP_ITEM_TYPE.POWERUP);
        if(c.getStatusCode() == HttpStatus.OK) {
            List<Shop> POWERUP = (List) c.getBody();
            map.put(SHOP_ITEM_TYPE.POWERUP, POWERUP);
        }
        ResponseEntity d = getOfType(SHOP_ITEM_TYPE.OTHER);
        if(d.getStatusCode() == HttpStatus.OK) {
            List<Shop> OTHER = (List) d.getBody();
            map.put(SHOP_ITEM_TYPE.OTHER, OTHER);
        }
        if(map.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    public ResponseEntity<Shop> getItem(String name) {
        Optional<Shop> item = shopRepository.findByName(name);
        return item.map(shop -> ResponseEntity.status(HttpStatus.OK).body(shop)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<Shop> getItem(long id) {
        Optional<Shop> item = shopRepository.findById(id);
        return item.map(shop -> ResponseEntity.status(HttpStatus.OK).body(shop)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public boolean doesExist(String name) {
        Optional<Shop> item = shopRepository.findByName(name);
        return item.isPresent();
    }

    public ResponseEntity<Shop> addItem(Shop shop) {
        if (doesExist(shop.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        shop.setPowerup(null);
        shopRepository.save(shop);
        return ResponseEntity.status(HttpStatus.CREATED).body(shop);
    }

    @Transactional
    public ResponseEntity<String> purchase(long uid, long shopId) {
        Optional<Account> accs = accountRepository.findById(uid);
        if(accs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }
        Account acc = accs.get();


        Optional<Shop> items = shopRepository.findById(shopId);
        if(items.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }
        Shop item = items.get();

        Optional<UserInventory> existing = userInventoryRepository
                .findByUserIdAndShopItemId(uid, shopId);

        if (existing.isPresent() && item.getItemType() != SHOP_ITEM_TYPE.POWERUP) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("You already own this item! Duplicate powerups are not allowed.");
        }

        if (acc.getTotalPoints() < item.getPrice()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough points");
        }

        acc.setTotalPoints(acc.getTotalPoints() - item.getPrice());
        accountRepository.save(acc);

        TransactionDTO transaction = new TransactionDTO();
        transaction.setDate(new Date());
        transaction.setPrice(item.getPrice());
        transaction.setUser(acc);
        transaction.setShopItem(item);
        long tId = transactionService.addTransaction(transaction);


        if(existing.isEmpty()){
            UserInventory ui = new UserInventory();
            ui.setUser(acc);
            ui.setShopItem(item);
            ui.setQuantity(1);
            ui.setEquipped(false);
            ui.setAcquiredAt(new Date());
            userInventoryRepository.save(ui);
        } else {
            UserInventory inv = existing.get();
            inv.setQuantity(inv.getQuantity() + 1);
            userInventoryRepository.save(inv);
        }



        if(item.getItemType() == SHOP_ITEM_TYPE.POWERUP) {
            powerupService.addToAcc(item.getPowerup().getId(), uid);
        }
        String success = String.format("Sucessfully purchased! Item transaction id: %d", tId);
        return ResponseEntity.ok(success);
    }

    public ResponseEntity<Shop> addPowerupItem(PowerupShopDTO psDTO) {

        boolean shopExists = shopRepository.findByName(psDTO.getShopName()).isPresent();

        if (shopExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }


        Optional<Powerup> powerup = powerupRepository.findByName(psDTO.getPowerupName());
        if (powerup.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (shopRepository.existsByPowerup(powerup.get())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Shop s = new Shop();
        s.setItemType(SHOP_ITEM_TYPE.POWERUP);
        s.setPowerup(powerup.get());
        s.setName(psDTO.getShopName());
        s.setImage(psDTO.getImage());
        s.setPrice(psDTO.getPrice());
        s.setDescription(psDTO.getDescription());

        Shop saved = shopRepository.save(s);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    public ResponseEntity<List<Shop>> getOfType(SHOP_ITEM_TYPE itemType) {
        List<Shop> returnable = shopRepository.findAllByItemType(itemType);
        if (returnable.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(returnable);
    }

    @Transactional
    public ResponseEntity equip(String itemName, long uid) {

        Account acc = accountRepository.findById(uid).orElse(null);
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }

        Shop shopItem = shopRepository.findByName(itemName).orElse(null);
        if (shopItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }

        UserInventory inv = userInventoryRepository
                .findByUserIdAndShopItemId(uid, shopItem.getId())
                .orElse(null);

        if (inv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found in user's inventory.");
        }

        if (shopItem.getItemType() == SHOP_ITEM_TYPE.POWERUP) {

            PowerupResponseDTO dto = new PowerupResponseDTO();
            dto.setPowerup(shopItem.getPowerup());
            dto.setMessage("Powerup consumed.");

            if (inv.getQuantity() > 1) {
                inv.setQuantity(inv.getQuantity() - 1);
                userInventoryRepository.save(inv);
            } else {
                userInventoryRepository.delete(inv);
            }

            powerupService.addToAcc(shopItem.getPowerup().getId(), uid);

            return ResponseEntity.ok(dto);
        }

        if (inv.isEquipped()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item already equipped.");
        }

        inv.setEquipped(true);
        userInventoryRepository.save(inv);

        ShopResponseDTO dto = new ShopResponseDTO();
        dto.setMessage("Item equipped successfully.");
        dto.setShop(shopItem);

        return ResponseEntity.ok(dto);
    }


    @Transactional
    public ResponseEntity unequip(String itemName, long uid) {

        Account acc = accountRepository.findById(uid).orElse(null);
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }

        Shop shopItem = shopRepository.findByName(itemName).orElse(null);
        if (shopItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }

        UserInventory inv = userInventoryRepository
                .findByUserIdAndShopItemId(uid, shopItem.getId())
                .orElse(null);

        if (inv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found in user's inventory.");
        }

        if (shopItem.getItemType() == SHOP_ITEM_TYPE.POWERUP) {
            PowerupResponseDTO dto = new PowerupResponseDTO();
            dto.setPowerup(null);
            dto.setMessage("Powerups cannot be unequipped.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(dto);
        }

        if (!inv.isEquipped()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Item is already unequipped.");
        }

        inv.setEquipped(false);
        userInventoryRepository.save(inv);

        return ResponseEntity.ok("Item unequipped successfully.");
    }

    @Transactional
    public ResponseEntity userPowerup(String itemName, long uid) {

        Account acc = accountRepository.findById(uid).orElse(null);
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }

        Shop shopItem = shopRepository.findByName(itemName).orElse(null);
        if (shopItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }

        if (shopItem.getItemType() != SHOP_ITEM_TYPE.POWERUP) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This item is not a powerup.");
        }

        UserInventory inv = userInventoryRepository
                .findByUserIdAndShopItemId(uid, shopItem.getId())
                .orElse(null);

        if (inv == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Powerup not in user's inventory.");
        }

        Powerup powerup = shopItem.getPowerup();

        if (inv.getQuantity() > 1) {
            inv.setQuantity(inv.getQuantity() - 1);
            userInventoryRepository.save(inv);
        } else {
            userInventoryRepository.delete(inv);
        }

        powerupService.addToAcc(powerup.getId(), uid);

        PowerupResponseDTO dto = new PowerupResponseDTO();
        dto.setPowerup(powerup);
        dto.setMessage("Powerup used successfully.");

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity deleteById(long id){
        Optional<Shop> s = shopRepository.findById(id);
        if (s.isPresent()) {
            userInventoryRepository.deleteAllByShopItem(s.get());
            transactionsRepository.deleteAllByShopItem(s.get());
            rewardRepository.deleteByShopItem(s.get());
            shopRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
    }

    public ResponseEntity deleteFromInventory(long userId, long shopItemId) {

        Optional<Shop> s = shopRepository.findById(shopItemId);
        if (s.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Shop item not found.");
        }

        Optional<UserInventory> entry =
                userInventoryRepository.findByUserIdAndShopItemId(userId, shopItemId);

        if (entry.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Shop item not found in user's inventory.");
        }

        userInventoryRepository.deleteByUserIdAndShopItem(userId, s.get());

        return ResponseEntity.status(HttpStatus.OK).build();
    }



}

    /**public ResponseEntity<String> purchaseControl(PaymentDTO paymentDTO) {
        Optional<Shop> item = shopRepository.findById(paymentDTO.getShopid());
        Optional<Account> acc = accountRepository.findById(paymentDTO.getUserid());
        if (item.isEmpty() || acc.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        long priceInCents = (long)(item.get().getPrice() * 100);

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount(priceInCents)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.get().getName())
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .addLineItem(lineItem)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("https://example.com/success")
                        .setCancelUrl("https://example.com/cancel")
                        .putMetadata("userId", String.valueOf(paymentDTO.getUserid()))
                        .putMetadata("shopId", String.valueOf(paymentDTO.getShopid()))
                        .build();

        Session session;
        try{
            session = Session.create(params);
        } catch (StripeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(session.getUrl());
    }**/


