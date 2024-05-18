package hr.leapwise.simplebankingsystem.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ImportDataRunner implements ApplicationRunner {

    @Autowired
    private TransactionImporter transactionImporter;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        transactionImporter.importTransactions("src/main/resources/transactions.csv");
    }
}

