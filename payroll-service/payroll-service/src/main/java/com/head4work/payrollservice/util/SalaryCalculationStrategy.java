package com.head4work.payrollservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

import static com.head4work.accountantcrm.utils.TimeUtil.isBetweenDates;

public interface SalaryCalculationStrategy {

    //TODO get actual salary for pay period
    // This currently return last assigned salary
    default Double getSalary(Salary currentSalary) {
        return currentSalary.getSalary();

//        List<Salary> salaries = employee.getSalaries();
//        if (!salaries.isEmpty()) {
//            TimeUtil.sortSalaryByDate(salaries);
//            return salaries.get(salaries.size() - 1).getSalary();
//        }
//        throw new RuntimeException("Salary is not assigned to user with id: %s".formatted(employee.getId()));
    }

//    default double getSalaryPerHour(Employee employee, int minutes) {
//        double result = getSalary(employee) * minutes / 60.0;
//        return (double) Math.round(result * 100) / 100;
//    }

    double calculateWage(Salary currentSalary, Employee employee, LocalDate from, LocalDate till);

    @Component
    @RequiredArgsConstructor
    class TaskStrategy implements SalaryCalculationStrategy {

        @Override
        public double calculateWage(Salary currentSalary, Employee employee, LocalDate from, LocalDate till) {
            int hours = employee.getTasks().stream()
                    .filter(task -> (task.getStatus().equals(TaskCompletionStatus.CLOSED) && isBetweenDates(LocalDateTime.from(task.getFinished()),
                            from.atStartOfDay(), till.atStartOfDay())))
                    .map(Task::getActualResolutionTime)
                    .reduce(Integer::sum)
                    .orElse(0);
            return hours * getSalary(currentSalary);
            // return getSalaryPerHour(employee, minutes);
        }
    }

    @Component
    class FixedStrategy implements SalaryCalculationStrategy {
        @Override
        public double calculateWage(Salary currentSalary, Employee employee, LocalDate from, LocalDate till) {
            return getSalary(currentSalary);
        }
    }

    @Component
    @RequiredArgsConstructor
    class TimeCardStrategy implements SalaryCalculationStrategy {
        @Value("${overtime.coefficient}")
        private double coefficient;

        @Override
        public double calculateWage(Salary currentSalary, Employee employee, LocalDate from, LocalDate till) {
            int hours = employee.getTimeCards().stream()
                    .filter(timeCard -> TimeUtil.isBetweenDates(timeCard.getDate().atStartOfDay(), from.atStartOfDay(), till.atStartOfDay()))
                    .map(new Function<TimeCard, Integer>() {
                        @Override
                        public Integer apply(TimeCard timeCard) {
                            int result = (int) Duration.between(timeCard.getClockIn(), timeCard.getClockOut()).toHours();
                            if (result > 8) {
                                result = (int) Math.round((result - 8) * coefficient + 8);
                            }
                            return result;
                        }
                    })
                    .reduce(Integer::sum).orElse(0);
            return hours * getSalary(currentSalary);
            //  return getSalaryPerHour(employee, minutes);
        }


    }
}
