package hr.leapwise.simplebankingsystem.util;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class SQLInsertGenerator {

    private static final int NUM_CUSTOMERS = 100;
    private static final int MAX_ACCOUNTS_PER_CUSTOMER = 1;
    private static final String[] ACCOUNT_TYPES = {"Savings", "Checking", "Credit"};
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        generateSQLInserts("src/main/resources/data.sql");
    }

    /**
     * Generates SQL inserts for customers and their accounts and writes them to a file.
     *
     * @param  fileName  the name of the file to write the SQL inserts to
     */
    public static void generateSQLInserts(String fileName) {
        Faker faker = new Faker();
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int customerId = 1; customerId <= NUM_CUSTOMERS; customerId++) {
                String name = faker.name().fullName().replace("'", "");
                String address = faker.address().streetAddress().replace("'", "");
                String email = StringUtils.deleteWhitespace(name.toLowerCase()) + customerId + "@example.com";
                String phoneNumber = faker.phoneNumber().phoneNumber();

                writer.write(String.format(
                        "INSERT INTO Customer (CUSTOMER_ID , NAME, ADDRESS, EMAIL, PHONE_NUMBER) VALUES (%d, '%s', '%s', '%s', '%s');\n",
                        customerId, name, address, email, phoneNumber
                ));

                int numAccounts = RANDOM.nextInt(MAX_ACCOUNTS_PER_CUSTOMER) + 1;
                for (int j = 0; j < numAccounts; j++) {
                    long accountId = customerId;
                    String accountNumber = faker.finance().iban("HR");
                    String accountType = ACCOUNT_TYPES[RANDOM.nextInt(ACCOUNT_TYPES.length)];
                    double balance = Math.round(RANDOM.nextDouble() * 10000 * 100) / 100.0; // Random balance up to 10000 with 2 decimal places
                    double pastMonthTurnover = Math.round(RANDOM.nextDouble() * 5000 * 100) / 100.0; // Random turnover up to 5000 with 2 decimal places

                    writer.write(String.format(
                            "INSERT INTO Account (ACCOUNT_ID, ACCOUNT_NUMBER, ACCOUNT_TYPE, BALANCE, PAST_MONTH_TURNOVER , CUSTOMER_ID, CURRENCY_ID) VALUES (%d, '%s', '%s', %.2f, %.2f, %d, '%s');\n",
                            accountId, accountNumber, accountType, balance, pastMonthTurnover, customerId, "EUR"
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
