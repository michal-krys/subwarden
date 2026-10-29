package com.subwarden.subscription;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CostCalculator {

    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);

    public Map<String, CostSummary> summarizeByCurrency(List<Subscription> subscriptions) {
        Map<String, BigDecimal> annualByCurrency = new TreeMap<>();

        for (Subscription subscription : subscriptions) {
            if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
                continue;
            }
            BigDecimal annualCost = subscription.getBillingCycle().annualCost(subscription.getPrice());
            annualByCurrency.merge(subscription.getCurrency(), annualCost, BigDecimal::add);
        }

        Map<String, CostSummary> result = new TreeMap<>();
        annualByCurrency.forEach((currency, annual) -> {
            BigDecimal monthly = annual.divide(MONTHS_IN_YEAR, 2, RoundingMode.HALF_UP);
            result.put(currency, new CostSummary(annual, monthly));
        });
        return result;
    }
}
