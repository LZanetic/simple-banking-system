package hr.leapwise.simplebankingsystem.dao;

import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE" +
            " (t.receiverAccountId = :accountId OR t.senderAccountId = :accountId)" +
            " AND (:currencyId IS NULL OR t.currencyId = :currencyId)" +
            " AND (:message IS NULL OR t.message LIKE :message)" +
            " AND (:senderAccountId IS NULL OR t.senderAccountId = :senderAccountId)" +
            " AND (:receiverAccountId IS NULL OR t.receiverAccountId = :receiverAccountId)" +
            " AND (:dateBefore IS NULL OR t.timestamp <= :dateBefore)" +
            " AND (:dateAfter IS NULL OR t.timestamp > :dateAfter)" +
            " AND (:amountGreaterThan IS NULL OR t.amount >= :amountGreaterThan)" +
            " AND (:amountLessThan IS NULL OR t.amount < :amountLessThan)" +
            " ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountIdAndParams(Long accountId, String currencyId, String message, Long senderAccountId, Long receiverAccountId, LocalDateTime dateBefore, LocalDateTime dateAfter, BigDecimal amountGreaterThan, BigDecimal amountLessThan);
}
