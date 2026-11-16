package com.subwarden.subscription;

import java.math.BigDecimal;
import java.time.LocalDate;

public enum BillingCycle {
    MONTHLY(12),
    QUARTERLY(4),
    YEARLY(1);

    private final int paymentsPerYear;

    BillingCycle(int paymentsPerYear) {
        this.paymentsPerYear = paymentsPerYear;
    }

    public BigDecimal annualCost(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(paymentsPerYear));
    }

    public LocalDate nextPaymentDate(LocalDate current, int anchorDayOfMonth) {
        LocalDate shifted = current.plusMonths(12 / paymentsPerYear);
        int day = Math.min(anchorDayOfMonth, shifted.lengthOfMonth());
        return shifted.withDayOfMonth(day);
    }
}
