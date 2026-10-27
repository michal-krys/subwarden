package com.subwarden.subscription;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BillingCycleTest {

    private static final BigDecimal PRICE = new BigDecimal("10.00");

    @Test
    void shouldCalculateAnnualCostForMonthlyCycle() {
        assertThat(BillingCycle.MONTHLY.annualCost(PRICE)).isEqualByComparingTo("120.00");
    }

    @Test
    void shouldCalculateAnnualCostForQuarterlyCycle() {
        assertThat(BillingCycle.QUARTERLY.annualCost(PRICE)).isEqualByComparingTo("40.00");
    }

    @Test
    void shouldCalculateAnnualCostForYearlyCycle() {
        assertThat(BillingCycle.YEARLY.annualCost(PRICE)).isEqualByComparingTo("10.00");
    }
}
