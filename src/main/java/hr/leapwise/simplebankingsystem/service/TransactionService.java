package hr.leapwise.simplebankingsystem.service;

import hr.leapwise.simplebankingsystem.model.dto.CustomerTransactionsDTO;
import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    Long createTransaction(TransactionDTO transactionDTO);

    CustomerTransactionsDTO getCustomerTransactionHistory(Long customerId, Map<String, String> filterParams);
}
