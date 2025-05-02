package com.head4work.payrollservice.util;

import com.head4work.payrollservice.dtos.PayrollDates;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public interface PayPeriodStrategy {
    //TODO filter by desired dates (for now it works from schedule start date end of the year)
    List<PayrollDates> schedulePayments(LocalDate startDate);

    default List<PayrollDates> getSchedule(int daysPeriod, LocalDate startDate) {
        List<PayrollDates> payrollDates = new ArrayList<>();
        LocalDate start = startDate;
        while (start.isBefore(LocalDate.now().withMonth(12).withDayOfMonth(31))) {
            LocalDate end = start.plusDays(daysPeriod);
            LocalDate payDate = getPaymentDate(end.plusDays(2));
            payrollDates.add(new PayrollDates(start, end, payDate));
            start = start.plusDays(daysPeriod);
        }
        return payrollDates;
    }

    // adjust payment day if it applied to weekend
    default LocalDate getPaymentDate(LocalDate payDate) {
        if (payDate.getDayOfWeek().getValue() >= 6) {
            payDate = payDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        }
        return payDate;
    }

    @Component
    public class WeeklyPaymentPeriodStrategy implements PayPeriodStrategy {

        @Override
        public List<PayrollDates> schedulePayments(LocalDate startDate) {
            // Implement weekly payment processing logic
            int daysPeriod = 7;
            return getSchedule(daysPeriod, startDate);
        }
    }

    @Component
    public class BiWeeklyPaymentPeriodStrategy implements PayPeriodStrategy {

        @Override
        public List<PayrollDates> schedulePayments(LocalDate startDate) {
            // Implement bi-weekly payment processing logic
            int daysPeriod = 14;
            return getSchedule(daysPeriod, startDate);
        }
    }


    @Component
    public class MonthlyPaymentPeriodStrategy implements PayPeriodStrategy {

        @Override
        public List<PayrollDates> schedulePayments(LocalDate startDate) {
            // Implement monthly payment processing logic
            List<PayrollDates> payrollDates = new ArrayList<>();
            LocalDate start = startDate;
            while (start.isBefore(LocalDate.now().withMonth(12).withDayOfMonth(31))) {
                LocalDate end = start.plusMonths(1);
                LocalDate payDate = getPaymentDate(end.plusDays(2));
                payrollDates.add(new PayrollDates(start, end, payDate));
                start = start.plusMonths(1);
            }
            return payrollDates;
        }
    }

}
