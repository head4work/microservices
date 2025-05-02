package com.head4work.payrollservice.controllers;

import com.head4work.payrollservice.dtos.EmployeeResponse;
import com.head4work.payrollservice.dtos.PayrollDates;
import com.head4work.payrollservice.entities.EmployeeClient;
import com.head4work.payrollservice.entities.Payroll;
import com.head4work.payrollservice.entities.Schedule;
import com.head4work.payrollservice.enums.PaymentPeriod;
import com.head4work.payrollservice.exceptions.EmployeeNotFoundException;
import com.head4work.payrollservice.services.ScheduleService;
import com.head4work.payrollservice.util.StrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/service/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final EmployeeClient employeeClient;
    private final StrategyFactory strategyFactory;
    private final ScheduleService scheduleService;

    @GetMapping("/schedule/{id}")
    public ResponseEntity<List<Payroll>> calculatePayrollsForSchedule(@PathVariable String id) {
        Schedule schedule = scheduleService.getSchedule(id);
        List<Payroll> payrolls = new ArrayList<>();
        List<EmployeeResponse> employees = employeeClient.getEmployeesByIds(schedule.getEmployeeIds());
        if (employees.isEmpty()) {
            throw new RuntimeException("No employees assigned to the schedule");
        }
        List<PayrollDates> payrollDates = getPayrollDatesForRestOfTheYear(schedule.getType(), schedule.getStartDate());

        for (PayrollDates payrollDate : payrollDates) {

            employees.forEach(employee -> {
                Payroll payroll = new Payroll();

                if (payrollDates != null) {
                    Payroll.builder()
                            .employeeId(employee.getId())
                            .startPeriod(payrollDates.getStartDate())
                            .endPeriod(payrollDates.getEndDate())
                            .payDate(payrollDates.getPayrollDate())
                            .employeeRate(employee.getSalaryAmount())
                            .paymentAmount(calculatePayAmount())
                            .build();
                }
            });
        }

        return ResponseEntity.ok(payrolls);
    }

    private List<PayrollDates> getPayrollDatesForRestOfTheYear(PaymentPeriod type, LocalDate startDate) {
        return strategyFactory.getPayPeriodStrategy(type).schedulePayments(startDate);
    }

    private Double calculatePayAmount() {
        return 0.0;
    }

}
