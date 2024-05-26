package hr.leapwise.simplebankingsystem.controller;

import hr.leapwise.simplebankingsystem.model.dto.CustomerTransactionsDTO;
import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Long> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionDTO));
    }

    @GetMapping("/history/{customerId}")
    public ResponseEntity<CustomerTransactionsDTO> getCustomerTransactionHistory(@PathVariable Long customerId, @RequestParam Map<String, String> filterParams) {
        return ResponseEntity.ok(transactionService.getCustomerTransactionHistory(customerId, filterParams));
    }
}
