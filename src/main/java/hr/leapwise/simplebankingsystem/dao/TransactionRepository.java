package hr.leapwise.simplebankingsystem.dao;

import hr.leapwise.simplebankingsystem.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
