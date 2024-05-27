package hr.leapwise.simplebankingsystem.service;

import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.CustomerRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.exception.IncorrectParamException;
import hr.leapwise.simplebankingsystem.mapper.CustomerMapper;
import hr.leapwise.simplebankingsystem.mapper.FilterParamMapper;
import hr.leapwise.simplebankingsystem.mapper.TransactionMapper;
import hr.leapwise.simplebankingsystem.model.dto.CustomerTransactionsDTO;
import hr.leapwise.simplebankingsystem.model.dto.TransactionDTO;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Customer;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.impl.TransactionServiceImpl;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class TransactionServiceTest {
    @Test
    public void verify_successful_transaction_creation() {
        TransactionServiceImpl service = new TransactionServiceImpl();
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderAccountId(1L);
        transactionDTO.setReceiverAccountId(2L);
        transactionDTO.setAmount(new BigDecimal("100.00"));
        transactionDTO.setCurrencyId("USD");

        Account senderAccount = new Account(1L, "123456", "Checking", 200.00, 0.0, "USD", 1L);
        Account receiverAccount = new Account(2L, "654321", "Savings", 150.00, 0.0, "USD", 2L);
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        TransactionMapper transactionMapper = TransactionMapper.INSTANCE;
        ApplicationEventPublisher applicationEventPublisher = Mockito.mock(ApplicationEventPublisher.class);

        ReflectionTestUtils.setField(service, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(service, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(service, "transactionMapper", transactionMapper);
        ReflectionTestUtils.setField(service, "customerRepository", customerRepository);
        ReflectionTestUtils.setField(service, "eventPublisher", applicationEventPublisher);

        Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(senderAccount));
        Mockito.when(accountRepository.findById(2L)).thenReturn(Optional.of(receiverAccount));
        Mockito.when(customerRepository.findById(any())).thenReturn(Optional.of(new Customer(1L, "John Doe", "123 Main St", "Gmz9e@example.com", "555-1234", List.of(senderAccount))));
        Mockito.when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.doNothing().when(applicationEventPublisher).publishEvent(any());

        service.createTransaction(transactionDTO);

        assertEquals(100.00, senderAccount.getBalance(), 0.01);
        assertEquals(250.00, receiverAccount.getBalance(), 0.01);
    }

    // Throw IncorrectParamException if sender and receiver account IDs are the same
    @Test
    public void handle_same_sender_and_receiver_accounts() {
        TransactionServiceImpl service = new TransactionServiceImpl();
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setSenderAccountId(1L);
        transactionDTO.setReceiverAccountId(1L);
        transactionDTO.setAmount(new BigDecimal("100.00"));
        transactionDTO.setCurrencyId("EUR");
        TransactionMapper transactionMapper = TransactionMapper.INSTANCE;
        ReflectionTestUtils.setField(service, "transactionMapper", transactionMapper);

        assertThrows(IncorrectParamException.class, () -> service.createTransaction(transactionDTO));
    }


    @Test
    public void test_valid_customer_and_filter_params() {
        Long customerId = 1L;
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("currencyId", "USD");
        TransactionServiceImpl service = new TransactionServiceImpl();
        Customer customer = new Customer(customerId, "John Doe", "1234 Main St", "john@example.com", "555-1234", List.of(new Account()));
        List<Account> accounts = List.of(new Account(1L, "123456", "Checking", 1000.0, 500.0, "EUR", customerId));
        List<Transaction> transactions = List.of(new Transaction(1L, BigDecimal.valueOf(100), "EUR", "Payment", LocalDateTime.now(), 1L, 2L));
        CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
        TransactionMapper transactionMapper = TransactionMapper.INSTANCE;
        CustomerMapper customerMapper = CustomerMapper.INSTANCE;
        FilterParamMapper filterParamMapper = FilterParamMapper.INSTANCE;

        ReflectionTestUtils.setField(service, "customerRepository", customerRepository);
        ReflectionTestUtils.setField(service, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(service, "transactionRepository", transactionRepository);
        ReflectionTestUtils.setField(service, "transactionMapper", transactionMapper);
        ReflectionTestUtils.setField(service, "customerMapper", customerMapper);
        ReflectionTestUtils.setField(service, "filterParamMapper", filterParamMapper);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(accountRepository.findByCustomerId(customerId)).thenReturn(accounts);
        Mockito.when(transactionRepository.findByAccountIdAndParams(1L, "USD", null, null, null, null, null, null, null)).thenReturn(transactions);
        CustomerTransactionsDTO result = service.getCustomerTransactionHistory(customerId, filterParams);

        assertNotNull(result);
        assertEquals(1, result.getTransactions().size());
        assertEquals("John Doe", result.getCustomer().getName());
    }
}
