package com.head4work.payrollservice.util;

import com.head4work.payrollservice.dtos.EmployeeResponse;
import com.head4work.payrollservice.dtos.PayrollDates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public interface SalaryCalculationStrategy {

    double calculateWage(EmployeeResponse employee, PayrollDates payrollDates);

    @Component
    @RequiredArgsConstructor
    class TaskStrategy implements SalaryCalculationStrategy {

        @Override
        public double calculateWage(EmployeeResponse employee, PayrollDates payrollDates) {
            //TODO get actual completed tasks
            int hours = 10;
//                    employee.getTasks().stream()
//                    .filter(task -> (task.getStatus().equals(TaskCompletionStatus.CLOSED) && isBetweenDates(LocalDateTime.from(task.getFinished()),
//                            from.atStartOfDay(), till.atStartOfDay())))
//                    .map(Task::getActualResolutionTime)
//                    .reduce(Integer::sum)
//                    .orElse(0);
            return hours * employee.getRate();
            // return getSalaryPerHour(employee, minutes);
        }
    }

    @Component
    class FixedStrategy implements SalaryCalculationStrategy {
        @Override
        public double calculateWage(EmployeeResponse employee, PayrollDates payrollDates) {
            return employee.getRate();
        }
    }

    @Component
    @RequiredArgsConstructor
    class TimeCardStrategy implements SalaryCalculationStrategy {
        @Value("${overtime.coefficient}")
        private double coefficient;

        @Override
        public double calculateWage(EmployeeResponse employee, PayrollDates payrollDates) {
            int hours = 10;
            //TODO actual logic to calculate hours
//                    employee.getTimeCards().stream()
//                    .filter(timeCard -> TimeUtil.isBetweenDates(timeCard.getDate().atStartOfDay(), from.atStartOfDay(), till.atStartOfDay()))
//                    .map(new Function<TimeCard, Integer>() {
//                        @Override
//                        public Integer apply(TimeCard timeCard) {
//                            int result = (int) Duration.between(timeCard.getClockIn(), timeCard.getClockOut()).toHours();
//                            if (result > 8) {
//                                result = (int) Math.round((result - 8) * coefficient + 8);
//                            }
//                            return result;
//                        }
//                    })
//                    .reduce(Integer::sum).orElse(0);
            return hours * employee.getRate();
            //  return getSalaryPerHour(employee, minutes);
        }


    }
}
