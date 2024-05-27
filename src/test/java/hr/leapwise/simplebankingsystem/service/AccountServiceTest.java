package hr.leapwise.simplebankingsystem.service;

import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.impl.AccountServiceImpl;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccountServiceTest {

    @Test
    public void verify_turnover_update_for_all_accounts() {
        // Setup
        AccountRepository mockAccountRepository = Mockito.mock(AccountRepository.class);
        TransactionRepository mockTransactionRepository = Mockito.mock(TransactionRepository.class);
        AccountService accountService = new AccountServiceImpl();
        ReflectionTestUtils.setField(accountService, "accountRepository", mockAccountRepository);
        ReflectionTestUtils.setField(accountService, "transactionRepository", mockTransactionRepository);

        Account account1 = new Account(1L, "123456", "Checking", 1000.0, 0.0, "USD", 1L);
        Account account2 = new Account(2L, "654321", "Savings", 2000.0, 0.0, "USD", 2L);
        List<Account> accounts = Arrays.asList(account1, account2);

        Mockito.when(mockAccountRepository.findAll()).thenReturn(accounts);

        List<Transaction> sentTransactions = List.of(
                new Transaction(1L, new BigDecimal("200"), "EUR", "Payment", LocalDateTime.now(), 1L, 2L)
        );
        List<Transaction> receivedTransactions = List.of(
                new Transaction(2L, new BigDecimal("500"), "EUR", "Deposit", LocalDateTime.now(), 2L, 1L)
        );

        Mockito.when(mockTransactionRepository.findByAllSentByDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(sentTransactions);
        Mockito.when(mockTransactionRepository.findByAllReceivedByDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(receivedTransactions);

        // Act
        accountService.updateTurnover();

        // Assert
        assertEquals(300.0, account1.getPastMonthTurnover(), 0.01);
        assertEquals(300.0, account2.getPastMonthTurnover(), 0.01);
    }

    // Handle cases where no transactions exist for an account in the past month
    @Test
    public void handle_no_transactions_for_past_month() {
        // Setup
        AccountRepository mockAccountRepository = Mockito.mock(AccountRepository.class);
        TransactionRepository mockTransactionRepository = Mockito.mock(TransactionRepository.class);
        AccountServiceImpl accountService = new AccountServiceImpl();
        ReflectionTestUtils.setField(accountService, "accountRepository", mockAccountRepository);
        ReflectionTestUtils.setField(accountService, "transactionRepository", mockTransactionRepository);

        Account account = new Account(1L, "123456", "Checking", 1000.0, 0.0, "USD", 1L);
        List<Account> accounts = Collections.singletonList(account);

        Mockito.when(mockAccountRepository.findAll()).thenReturn(accounts);
        Mockito.when(mockTransactionRepository.findByAllSentByDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        Mockito.when(mockTransactionRepository.findByAllReceivedByDate(Mockito.anyLong(), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        accountService.updateTurnover();

        // Assert
        assertEquals(0.0, account.getPastMonthTurnover(), 0.01);
    }

}

