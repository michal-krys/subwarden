package com.subwarden.subscription;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SubscriptionRepositoryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Subscription buildSubscription(String name, LocalDate nextPaymentDate, SubscriptionStatus status) {
        Subscription subscription = new Subscription();
        subscription.setName(name);
        subscription.setPrice(new BigDecimal("10.00"));
        subscription.setCurrency("PLN");
        subscription.setCategory(SubscriptionCategory.OTHER);
        subscription.setBillingCycle(BillingCycle.MONTHLY);
        subscription.setNextPaymentDate(nextPaymentDate);
        subscription.setStatus(status);
        return subscription;
    }

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

    @Test
    void shouldFindOnlyActiveSubscriptionsDueInGivenRange() {
        //GIVEN
        LocalDate from = LocalDate.of(2026,11,1);
        LocalDate to = LocalDate.of(2026,11,7);

        subscriptionRepository.save(buildSubscription("Due on first day", from, SubscriptionStatus.ACTIVE));
        subscriptionRepository.save(buildSubscription("Due on last day", to, SubscriptionStatus.ACTIVE));
        subscriptionRepository.save(buildSubscription("Due too late", to.plusDays(1), SubscriptionStatus.ACTIVE));
        subscriptionRepository.save(buildSubscription("Cancelled", from.plusDays(2), SubscriptionStatus.CANCELLED));
        entityManager.flush();
        entityManager.clear();

        //WHEN
        List<Subscription> result = subscriptionRepository.findByStatusAndNextPaymentDateBetween(SubscriptionStatus.ACTIVE, from, to);

        //THEN
        assertThat(result)
                .extracting(Subscription::getName)
                .containsExactlyInAnyOrder("Due on first day", "Due on last day");
    }
}
