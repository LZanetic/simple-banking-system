package hr.leapwise.simplebankingsystem.dao;

import hr.leapwise.simplebankingsystem.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);
}

