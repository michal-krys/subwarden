package com.subwarden.subscription;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

    @Test
    void shouldMoveMonthlyPaymentToSameDayOfNextMonth() {
        assertThat(BillingCycle.MONTHLY.nextPaymentDate(LocalDate.of(2026, 1, 15), 15))
                .isEqualTo(LocalDate.of(2026, 2, 15));
    }

    @Test
    void shouldClampToLastDayOfShorterMonth() {
        assertThat(BillingCycle.MONTHLY.nextPaymentDate(LocalDate.of(2026, 1, 31), 31))
                .isEqualTo(LocalDate.of(2026, 2, 28));
    }

    @Test
    void shouldUseLeapDayInLeapYear() {
        assertThat(BillingCycle.MONTHLY.nextPaymentDate(LocalDate.of(2028, 1, 31), 31))
        .isEqualTo(LocalDate.of(2028, 2, 29));
    }

    @Test
    void shouldReturnToAnchorDayAfterShortMonth() {
        assertThat(BillingCycle.MONTHLY.nextPaymentDate(LocalDate.of(2026, 2, 28), 31))
                .isEqualTo(LocalDate.of(2026, 3, 31));
    }

    @Test
    void shouldHandleQuarterlyCycleAcrossYearBoundary() {
        assertThat(BillingCycle.QUARTERLY.nextPaymentDate(LocalDate.of(2026, 11, 30), 30))
                .isEqualTo(LocalDate.of(2027, 2, 28));
    }

    @Test
    void shouldHandleYearlyCycleFormLeapDay() {
        assertThat(BillingCycle.YEARLY.nextPaymentDate(LocalDate.of(2028, 2, 29), 29))
                .isEqualTo(LocalDate.of(2029, 2, 28));
    }
}
