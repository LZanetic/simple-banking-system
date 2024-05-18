package hr.leapwise.simplebankingsystem.dao;

import hr.leapwise.simplebankingsystem.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

