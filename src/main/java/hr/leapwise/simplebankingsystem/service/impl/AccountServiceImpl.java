package hr.leapwise.simplebankingsystem.service.impl;

import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import hr.leapwise.simplebankingsystem.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Updates the turnover for all accounts by subtracting the sum of sent transactions from the sum of received transactions
     * for the past month.
     */
    @Transactional
    @Override
    public void updateTurnover() {
        log.info("Started updating turnover at: {}", LocalDateTime.now());

        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);
        List<Account> accounts = accountRepository.findAll();

        accounts.parallelStream().forEach(account -> {
            List<Transaction> sentTransactions = transactionRepository.findByAllSentByDate(account.getAccountId(), startDate);
            List<Transaction> receivedTransactions = transactionRepository.findByAllReceivedByDate(account.getAccountId(), startDate);

            BigDecimal turnover = receivedTransactions
                    .stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .subtract(sentTransactions
                            .stream()
                            .map(Transaction::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));

            account.setPastMonthTurnover(turnover.doubleValue());
        });

        accountRepository.saveAll(accounts);
        log.info("Finished updating turnover at: {}", LocalDateTime.now());
    }
}
