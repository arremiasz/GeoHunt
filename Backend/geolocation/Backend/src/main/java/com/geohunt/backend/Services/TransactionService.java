package com.geohunt.backend.Services;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Transactions;
import com.geohunt.backend.database.TransactionsRepository;
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
}
