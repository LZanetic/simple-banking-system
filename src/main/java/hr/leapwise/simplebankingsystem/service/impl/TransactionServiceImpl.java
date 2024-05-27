package hr.leapwise.simplebankingsystem.service.impl;

import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.CustomerRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.event.TransactionProcessedEvent;
import hr.leapwise.simplebankingsystem.exception.IncorrectParamException;
import hr.leapwise.simplebankingsystem.exception.InsufficientFundsException;
import hr.leapwise.simplebankingsystem.exception.ResourceNotFoundException;
import hr.leapwise.simplebankingsystem.mapper.CustomerMapper;
import hr.leapwise.simplebankingsystem.mapper.FilterParamMapper;
import hr.leapwise.simplebankingsystem.mapper.TransactionMapper;
import hr.leapwise.simplebankingsystem.model.dto.CustomerTransactionsDTO;
import hr.leapwise.simplebankingsystem.model.dto.EmailDTO;
import hr.leapwise.simplebankingsystem.model.dto.FilterParamDTO;
import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Customer;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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

    @Autowired
    private ApplicationEventPublisher eventPublisher;


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
        if (Objects.equals(transaction.getSenderAccountId(), transaction.getReceiverAccountId()))
            throw new IncorrectParamException("Sender and receiver cannot be the same account");

        Account senderAccount = accountRepository.findById(transactionDTO.getSenderAccountId()).orElseThrow(
                () -> new ResourceNotFoundException("Sender Account not found"));
        Customer senderCustomer = customerRepository.findById(senderAccount.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Sender Customer not found"));
        Optional<Account> receiverAccountOpt = accountRepository.findById(transactionDTO.getReceiverAccountId());

        if(receiverAccountOpt.isEmpty()) {
            log.info("Receiver Account not found");
            eventPublisher.publishEvent(
                    new TransactionProcessedEvent(this, buildEmailDto(senderCustomer, transaction, senderAccount, "send", "failed")));
            throw new ResourceNotFoundException("Receiver Account not found");
        }
        Account receiverAccount = receiverAccountOpt.get();

        Optional<Customer> receiverCustomerOpt = customerRepository.findById(receiverAccount.getCustomerId());
        if(receiverCustomerOpt.isEmpty()) {
            log.info("Receiver Customer not found");
            eventPublisher.publishEvent(
                    new TransactionProcessedEvent(this, buildEmailDto(senderCustomer, transaction, senderAccount, "send", "failed")));
            throw new ResourceNotFoundException("Receiver Customer not found");
        }
        Customer receiverCustomer = receiverCustomerOpt.get();

        try {
            if (senderAccount.getBalance() < transaction.getAmount().doubleValue())
                throw new InsufficientFundsException("Insufficient funds");

            transaction.setSenderAccountId(senderAccount.getAccountId());
            transaction.setReceiverAccountId(receiverAccount.getAccountId());
            senderAccount.setBalance(senderAccount.getBalance() - transaction.getAmount().doubleValue());
            receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount().doubleValue());

            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);

            Long transactionId = transactionRepository.save(transaction).getTransactionId();
            log.info("Transaction created: {}", transaction);

            eventPublisher.publishEvent(
                    new TransactionProcessedEvent(this, buildEmailDto(senderCustomer, transaction, senderAccount, "send", "success")));
            eventPublisher.publishEvent(
                    new TransactionProcessedEvent(this, buildEmailDto(receiverCustomer, transaction, receiverAccount, "receive", "success")));
            return transactionId;
        } catch (Exception e){
            log.info("Transaction failed: {}", transaction);
            eventPublisher.publishEvent(
                    new TransactionProcessedEvent(this, buildEmailDto(senderCustomer, transaction, senderAccount, "send", "failed")));
            throw e;
        }
    }

    /**
     * Retrieves the transaction history of a customer.
     *
     * @param  customerId   the ID of the customer
     * @param  filterParams the filter parameters for the transactions
     * @return              the customer's transaction history
     * @throws ResourceNotFoundException if the customer or customer accounts are not found
     */
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

    /**
     * Builds an EmailDTO object based on the given customer, transaction, account, status, and action.
     *
     * @param  customer        the customer object
     * @param  transaction     the transaction object
     * @param  account         the account object
     * @param  status          the status of the transaction
     * @param  action          the action performed in the transaction
     * @return                 the EmailDTO object with the populated fields
     */
    private EmailDTO buildEmailDto(Customer customer, Transaction transaction, Account account, String status, String action) {
        BigDecimal newBalance;
        if(action.equalsIgnoreCase("send")) {
             newBalance = BigDecimal.valueOf(account.getBalance() - transaction.getAmount().doubleValue());
        } else {
             newBalance = BigDecimal.valueOf(account.getBalance() + transaction.getAmount().doubleValue());
        }

        if(Objects.equals(status, "failed")) newBalance = BigDecimal.valueOf(account.getBalance());

        return EmailDTO.builder()
                .to(customer.getEmail())
                .transactionId(transaction.getTransactionId())
                .balance(transaction.getAmount())
                .oldBalance(BigDecimal.valueOf(account.getBalance()))
                .newBalance(newBalance)
                .status(status)
                .action(action)
                .build();
    }
}
