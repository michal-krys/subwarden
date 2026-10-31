package com.subwarden.user;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldFindUserByEmail() {
        userRepository.saveAndFlush(new User("anna@example.com", "not-a-real-hash"));
        entityManager.clear();

        Optional<User> found = userRepository.findByEmail("anna@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("anna@example.com");
        assertThat(found.get().getCreatedAt()).isNotNull();
    }

    @Test
    void shouldRejectDuplicateEmail() {
       userRepository.saveAndFlush(new User("anna@example.com", "not-a-real-hash"));

       assertThatThrownBy(() -> userRepository.saveAndFlush(new User("anna@example.com", "other-hash")))
               .isInstanceOf(DataIntegrityViolationException.class);
    }
}
