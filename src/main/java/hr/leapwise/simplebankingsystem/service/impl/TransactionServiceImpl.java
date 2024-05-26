package hr.leapwise.simplebankingsystem.service.impl;

import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.CustomerRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.exception.IncorrectParamException;
import hr.leapwise.simplebankingsystem.exception.InsufficientFundsException;
import hr.leapwise.simplebankingsystem.exception.ResourceNotFoundException;
import hr.leapwise.simplebankingsystem.mapper.CustomerMapper;
import hr.leapwise.simplebankingsystem.mapper.FilterParamMapper;
import hr.leapwise.simplebankingsystem.mapper.TransactionMapper;
import hr.leapwise.simplebankingsystem.model.dto.CustomerTransactionsDTO;
import hr.leapwise.simplebankingsystem.model.dto.FilterParamDTO;
import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Customer;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FilterParamMapper filterParamMapper;

    @Autowired
    private CustomerMapper customerMapper;


    /**
     * Creates a new transaction and updates the sender and receiver accounts.
     *
     * @param  transactionDTO  the transaction data transfer object containing the transaction details
     * @return                 the ID of the newly created transaction
     * @throws IncorrectParamException  if the sender and receiver accounts are the same
     * @throws ResourceNotFoundException  if the sender or receiver account is not found
     * @throws InsufficientFundsException  if the sender account does not have sufficient funds
     */
    @Transactional
    @Override
    public Long createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = transactionMapper.mapToTransactionEntity(transactionDTO);
        if(Objects.equals(transaction.getSenderAccountId(), transaction.getReceiverAccountId())) throw new IncorrectParamException("Sender and receiver cannot be the same account");

        Account senderAccount = accountRepository.findById(transactionDTO.getSenderAccountId()).orElseThrow(() -> new ResourceNotFoundException("Sender Account not found"));
        Account receiverAccount = accountRepository.findById(transactionDTO.getReceiverAccountId()).orElseThrow(() -> new ResourceNotFoundException("Receiver Account not found"));

        if(senderAccount.getBalance() < transaction.getAmount().doubleValue()) throw new InsufficientFundsException("Insufficient funds");

        transaction.setSenderAccountId(senderAccount.getAccountId());
        transaction.setReceiverAccountId(receiverAccount.getAccountId());
        senderAccount.setBalance(senderAccount.getBalance() - transaction.getAmount().doubleValue());
        receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount().doubleValue());

        log.info("Transaction created: {}", transaction);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        return transactionRepository.save(transaction).getTransactionId();
    }

    @Override
    public CustomerTransactionsDTO getCustomerTransactionHistory(Long customerId, Map<String, String> filterParams) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        if(accounts.isEmpty()) throw new ResourceNotFoundException("Customer accounts not found");

        FilterParamDTO filterParamDTO = filterParamMapper.mapToFilterParamDTO(filterParams);
        List<Transaction> transactions = new ArrayList<>();

        accounts.forEach(account -> transactions.addAll(
                transactionRepository.findByAccountIdAndParams(
                account.getAccountId(),
                filterParamDTO.getCurrencyId(),
                filterParamDTO.getMessage(),
                filterParamDTO.getSenderAccountId(),
                filterParamDTO.getReceiverAccountId(),
                filterParamDTO.getDateBefore(),
                filterParamDTO.getDateAfter(),
                filterParamDTO.getAmountGreaterThan(),
                filterParamDTO.getAmountLessThan())));

        List<TransactionDTO> transactionDTOs = transactions
                .stream()
                .map(transactionMapper::mapToTransactionDTO)
                .toList();

        return new CustomerTransactionsDTO(customerMapper.mapToCustomerDTO(customer), transactionDTOs);
    }
}
