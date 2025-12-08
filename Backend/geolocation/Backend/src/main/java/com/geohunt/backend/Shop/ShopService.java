package com.geohunt.backend.Shop;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
//import com.stripe.exception.StripeException;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<String> deleteItem(String name) {
        Optional<Shop> s = shopRepository.findByName(name);
        if (s.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop Item not found.");
        }
        shopRepository.delete(s.get());
        //TODO Delete it from everyones inventory.
        return ResponseEntity.status(HttpStatus.OK).body("Shop Item deleted.");
    }

    public ResponseEntity<Shop> getItem(String name){
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
        shopRepository.save(shop);
        return ResponseEntity.status(HttpStatus.CREATED).body(shop);
    }

    public ResponseEntity<List<Shop>> getOfType(SHOP_ITEM_TYPE itemType) {
        List<Shop> returnable = shopRepository.findAllByItemType(itemType);
        if (returnable.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(returnable);
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
}

