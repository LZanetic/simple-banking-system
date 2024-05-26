package hr.leapwise.simplebankingsystem.service;

import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;

public interface TransactionService {

    Long createTransaction(TransactionDTO transactionDTO);
}
