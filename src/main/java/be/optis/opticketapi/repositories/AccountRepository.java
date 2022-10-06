package be.optis.opticketapi.repositories;

import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmail(String email);

    List<Account> findByRole(Role handyman);
}