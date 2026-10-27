package com.subwarden.subscription;

import java.math.BigDecimal;

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
}
