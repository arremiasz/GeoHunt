package com.geohunt.backend.Shop;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Shop.DTOs.TransactionDTO;
import com.geohunt.backend.database.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private AccountService accountService;

    public ResponseEntity<Transactions> getTransactionById(long id){
        Optional<Transactions> transaction = transactionsRepository.findById(id);
        return transaction.map(shop -> ResponseEntity.status(HttpStatus.OK).body(shop)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<List<Transactions>> getUsersTransactions(long userid){
        try{
            Account ac = accountService.getAccountById(userid);
            List<Transactions> transactions = transactionsRepository.findByUser(ac);
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public long addTransaction(TransactionDTO transaction){
        Transactions t = new Transactions();
        t.setDate(transaction.getDate());
        t.setPrice(transaction.getPrice());
        t.setUser(transaction.getUser());
        t.setShopItem(transaction.getShopItem());
        transactionsRepository.save(t);
        return t.getTransactionid();
    }

    public void deleteTransaction(long id){ //DEVONLY
        transactionsRepository.deleteById(id);
    }
}
