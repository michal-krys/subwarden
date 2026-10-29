package com.subwarden.subscription;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CostCalculatorTest {

    private final CostCalculator calculator = new CostCalculator();

    @Test
    void shouldReturnEmptyResultForNoSubscriptions() {
        Map<String, CostSummary> result = calculator.summarizeByCurrency(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSumCostsSeparatelyForEachCurrency() {
        List<Subscription> subscriptions = List.of (
                subscription("10.00", "PLN", BillingCycle.MONTHLY, SubscriptionStatus.ACTIVE),
                subscription("20.00", "PLN", BillingCycle.MONTHLY, SubscriptionStatus.ACTIVE),
                subscription("100.00", "EUR", BillingCycle.YEARLY, SubscriptionStatus.ACTIVE)
        );

        Map<String, CostSummary> result = calculator.summarizeByCurrency(subscriptions);

        assertThat(result).containsOnlyKeys("EUR", "PLN");
        assertThat(result.get("PLN").annual()).isEqualByComparingTo("360.00");
        assertThat(result.get("PLN").monthly()).isEqualByComparingTo("30.00");
        assertThat(result.get("EUR").annual()).isEqualByComparingTo("100.00");
        assertThat(result.get("EUR").monthly()).isEqualByComparingTo("8.33");
    }

    @Test
    void shouldIgnoreCancelledSubscriptions() {
        List<Subscription> subscriptions = List.of (
                subscription("10.00", "PLN", BillingCycle.MONTHLY, SubscriptionStatus.ACTIVE),
                subscription("50.00", "PLN", BillingCycle.MONTHLY, SubscriptionStatus.CANCELLED)
        );

        Map<String, CostSummary> result = calculator.summarizeByCurrency(subscriptions);

        assertThat(result.get("PLN").annual()).isEqualByComparingTo("120.00");
        assertThat(result.get("PLN").monthly()).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldRoundOnlyOnceAtTheEnd() {
        List<Subscription> subscriptions = List.of (
                subscription("100.00", "PLN", BillingCycle.YEARLY, SubscriptionStatus.ACTIVE),
                subscription("100.00", "PLN", BillingCycle.YEARLY, SubscriptionStatus.ACTIVE),
                subscription("100.00", "PLN", BillingCycle.YEARLY, SubscriptionStatus.ACTIVE)
        );

        Map<String, CostSummary> result = calculator.summarizeByCurrency(subscriptions);

        assertThat(result.get("PLN").annual()).isEqualByComparingTo("300.00");
        assertThat(result.get("PLN").monthly()).isEqualByComparingTo("25.00");
    }

    private Subscription subscription(String price, String currency, BillingCycle cycle, SubscriptionStatus status) {
        Subscription subscription = new Subscription();
        subscription.setPrice(new BigDecimal(price));
        subscription.setCurrency(currency);
        subscription.setBillingCycle(cycle);
        subscription.setStatus(status);
        return subscription;
    }
}