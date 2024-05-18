package hr.leapwise.simplebankingsystem.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CSVFileGenerator {

    private static final int NUM_RECORDS = 100000;
    private static final int NUM_ACCOUNTS = 100;
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "JPY", "HRK"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static void main(String[] args) {
        generateCSVFile("src/main/resources/transactions.csv");
    }

    /**
     * Generates a CSV file with transaction data.
     *
     * @param  fileName  the name of the file to be generated
     */
    public static void generateCSVFile(String fileName) {
        Random random = new Random();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("transactionId,senderAccountId,receiverAccountId,amount,currencyId,message,timestamp\n");

            for (int i = 1; i <= NUM_RECORDS; i++) {
                long senderAccountId = random.nextInt(NUM_ACCOUNTS) + 1;
                long receiverAccountId;
                do {
                    receiverAccountId = random.nextInt(NUM_ACCOUNTS) + 1;
                } while (senderAccountId == receiverAccountId); // Ensure sender and receiver are not the same

                double amount = Math.round(random.nextDouble() * 10000 * 100) / 100.0; // Random amount up to 10000 with 2 decimal places
                String currencyId = CURRENCIES[random.nextInt(CURRENCIES.length)];
                String message = "Sample transaction message " + i;
                String timestamp = LocalDateTime.now().minusDays(random.nextInt(365)).format(formatter);

                writer.write(String.format("%d,%d,%d,%.2f,%s,%s,%s\n", i, senderAccountId, receiverAccountId, amount, currencyId, message, timestamp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
