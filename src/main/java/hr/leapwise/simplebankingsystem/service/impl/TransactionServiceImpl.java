package hr.leapwise.simplebankingsystem.service.impl;

import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.exception.IncorrectParamException;
import hr.leapwise.simplebankingsystem.exception.ResourceNotFoundException;
import hr.leapwise.simplebankingsystem.mapper.TransactionMapper;
import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountRepository accountRepository;


    @Transactional
    @Override
    public Long createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.mapToTransactionEntity(transactionDTO);
        if(transaction.getSenderAccountId() == transaction.getReceiverAccountId()) throw new IncorrectParamException("Sender and receiver cannot be the same account");

        Account senderAccount = accountRepository.findById(transactionDTO.getSenderAccountId()).orElseThrow(() -> new ResourceNotFoundException("Sender Account not found"));
        Account receiverAccount = accountRepository.findById(transactionDTO.getReceiverAccountId()).orElseThrow(() -> new ResourceNotFoundException("Receiver Account not found"));

        if(senderAccount.getBalance() < transaction.getAmount()) throw new RuntimeException("Insufficient funds");

        transaction.setSenderAccountId(senderAccount.getAccountId());
        transaction.setReceiverAccountId(receiverAccount.getAccountId());
        senderAccount.setBalance(senderAccount.getBalance() - transaction.getAmount());
        receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount());

        log.info("Transaction created: {}", transaction);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        return transactionRepository.save(transaction).getTransactionId();
    }
}
