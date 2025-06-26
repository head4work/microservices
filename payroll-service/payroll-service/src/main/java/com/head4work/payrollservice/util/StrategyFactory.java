package com.head4work.payrollservice.util;


import com.head4work.payrollservice.enums.PaymentPeriod;
import com.head4work.payrollservice.enums.RateType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class StrategyFactory {
    private final Map<RateType, SalaryCalculationStrategy> salaryStrategyMap = new HashMap<>();
    private final Map<PaymentPeriod, PayPeriodStrategy> paymentPeriodStrategyMap = new HashMap<>();

    public StrategyFactory(SalaryCalculationStrategy.TaskStrategy taskStrategy,
                           SalaryCalculationStrategy.FixedStrategy fixedStrategy,
                           SalaryCalculationStrategy.TimeCardStrategy timeCardStrategy,
                           PayPeriodStrategy.BiWeeklyPaymentPeriodStrategy biWeeklyPaymentPeriodStrategy,
                           PayPeriodStrategy.MonthlyPaymentPeriodStrategy monthlyPaymentPeriodStrategy,
                           PayPeriodStrategy.WeeklyPaymentPeriodStrategy weeklyPaymentPeriodStrategy) {
        salaryStrategyMap.put(RateType.TASKS, taskStrategy);
        salaryStrategyMap.put(RateType.FIXED, fixedStrategy);
        salaryStrategyMap.put(RateType.HOURLY, timeCardStrategy);
        paymentPeriodStrategyMap.put(PaymentPeriod.BI_WEEKLY, biWeeklyPaymentPeriodStrategy);
        paymentPeriodStrategyMap.put(PaymentPeriod.MONTHLY, monthlyPaymentPeriodStrategy);
        paymentPeriodStrategyMap.put(PaymentPeriod.WEEKLY, weeklyPaymentPeriodStrategy);
    }

    public SalaryCalculationStrategy getSalaryCalculationStrategy(RateType RateType) {
        return salaryStrategyMap.get(RateType);
    }

    public PayPeriodStrategy getPayPeriodStrategy(PaymentPeriod paymentPeriod) {
        return paymentPeriodStrategyMap.get(paymentPeriod);
    }
}
