package com.subwarden.subscription;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSaveAndReadSubscriptionFromDatabase() {
        //GIVEN
        Subscription subscription = new Subscription();
        subscription.setName("Netflix");
        subscription.setDescription("Standard plan");
        subscription.setPrice(new BigDecimal("49.99"));
        subscription.setCurrency("PLN");
        subscription.setCategory(SubscriptionCategory.STREAMING);
        subscription.setBillingCycle(BillingCycle.MONTHLY);
        subscription.setNextPaymentDate(LocalDate.of(2026,11,1));
        subscription.setStatus(SubscriptionStatus.ACTIVE);

        Subscription saved = subscriptionRepository.save(subscription);
        entityManager.flush();
        entityManager.clear();

        //WHEN
        Subscription found = subscriptionRepository.findById(saved.getId()).orElseThrow();

        //THEN
        assertThat(found.getName()).isEqualTo("Netflix");
        assertThat(found.getPrice()).isEqualByComparingTo("49.99");
        assertThat(found.getCurrency()).isEqualTo("PLN");
        assertThat(found.getCategory()).isEqualTo(SubscriptionCategory.STREAMING);
        assertThat(found.getBillingCycle()).isEqualTo(BillingCycle.MONTHLY);
        assertThat(found.getNextPaymentDate()).isEqualTo(LocalDate.of(2026,11,1));
        assertThat(found.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

    }
}
