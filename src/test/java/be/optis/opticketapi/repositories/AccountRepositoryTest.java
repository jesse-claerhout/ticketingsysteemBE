package be.optis.opticketapi.repositories;

import be.optis.opticketapi.models.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository subject;

    @Test
    void findByEmail_AccountWithEmailExists_ReturnsAccount() {
        var email = "johnsm@cronos.be";
        var account = Account.builder()
                .firstName("John")
                .lastName("Smith")
                .email(email)
                .password("password")
                .build();
        subject.save(account);

        var actual = subject.findByEmail(email).orElse(null);

        assertEquals(account, actual);
    }

    @Test
    void findByEmail_AccountWithEmailDoesNotExist_ReturnsNull() {
        var email = "johnsm@cronos.be";

        var actual = subject.findByEmail(email).orElse(null);

        assertNull(actual);
    }
}