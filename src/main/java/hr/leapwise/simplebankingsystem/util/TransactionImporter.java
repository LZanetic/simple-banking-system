package hr.leapwise.simplebankingsystem.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import hr.leapwise.simplebankingsystem.dao.AccountRepository;
import hr.leapwise.simplebankingsystem.dao.TransactionRepository;
import hr.leapwise.simplebankingsystem.model.dto.TransactionCsvDTO;
import hr.leapwise.simplebankingsystem.model.entity.Account;
import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class TransactionImporter {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final int THREAD_POOL_SIZE = 10;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public void importTransactions(String fileName) throws IOException {
        List<TransactionCsvDTO> transactionDTOs = parseCSV(fileName);
        List<Transaction> transactions = mapToTransactions(transactionDTOs);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        int batchSize = transactions.size() / THREAD_POOL_SIZE;

        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            int start = i * batchSize;
            int end = (i == THREAD_POOL_SIZE - 1) ? transactions.size() : start + batchSize;

            List<Transaction> batch = transactions.subList(start, end);
            executor.submit(() -> saveTransactions(batch));
        }

        executor.shutdown();
    }

    private List<TransactionCsvDTO> parseCSV(String fileName) throws IOException {
        try (FileReader reader = new FileReader(fileName)) {
            CsvToBean<TransactionCsvDTO> csvToBean = new CsvToBeanBuilder<TransactionCsvDTO>(reader)
                    .withType(TransactionCsvDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }

    private List<Transaction> mapToTransactions(List<TransactionCsvDTO> transactionDTOs) {
        return transactionDTOs.stream().map(dto -> {
            Account senderAccount = accountRepository.findById(dto.getSenderAccountId())
                    .orElseThrow(() -> new RuntimeException("Sender Account not found: " + dto.getSenderAccountId()));
            Account receiverAccount = accountRepository.findById(dto.getReceiverAccountId())
                    .orElseThrow(() -> new RuntimeException("Receiver Account not found: " + dto.getReceiverAccountId()));

            LocalDateTime timestamp = LocalDateTime.parse(dto.getTimestamp(), formatter);

            return new Transaction(null, BigDecimal.valueOf(dto.getAmount()), dto.getCurrencyId(), dto.getMessage(), timestamp, senderAccount.getAccountId(), receiverAccount.getAccountId());
        }).collect(Collectors.toList());
    }

    @Transactional
    public void saveTransactions(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }
}

