package com.head4work.payrollservice.controllers;

import com.head4work.payrollservice.dtos.EmployeeResponse;
import com.head4work.payrollservice.dtos.PayrollDates;
import com.head4work.payrollservice.entities.Payroll;
import com.head4work.payrollservice.entities.Schedule;
import com.head4work.payrollservice.enums.PaymentPeriod;
import com.head4work.payrollservice.exceptions.CustomResponseException;
import com.head4work.payrollservice.services.EmployeeService;
import com.head4work.payrollservice.services.ScheduleService;
import com.head4work.payrollservice.util.StrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/service/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final EmployeeService employeeService;
    // private final EmployeeClient employeeClient;
    private final StrategyFactory strategyFactory;
    private final ScheduleService scheduleService;

    @PostMapping("/schedule")
    public ResponseEntity<Schedule> createSchedule(@RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.saveSchedule(schedule));
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<List<Payroll>> calculatePayrollsForSchedule(@PathVariable String id) {
        Schedule schedule = scheduleService.getSchedule(id);
        List<Payroll> payrolls = new ArrayList<>();
        List<EmployeeResponse> employees = employeeService.getEmployeesByIds(schedule.getEmployeeIds());
        if (employees.isEmpty()) {
            throw new CustomResponseException("No employees assigned to schedule", HttpStatus.BAD_REQUEST);
        }
        List<PayrollDates> payrollDates = getPayrollDatesForRestOfTheYear(schedule.getType(), schedule.getStartDate());

        payrollDates.forEach(payrollDate -> {
            for (EmployeeResponse employee : employees) {
                payrolls.add(Payroll.builder()
                        .employeeId(employee.getId())
                        .startPeriod(payrollDate.getStartDate())
                        .endPeriod(payrollDate.getEndDate())
                        .payDate(payrollDate.getPayrollDate())
                        .employeeRate(employee.getRate())
                        .paymentAmount(calculatePayAmountForPaymentDates(employee, payrollDate))
                        .build());
            }
        });
        return ResponseEntity.ok(payrolls);
    }

    private List<PayrollDates> getPayrollDatesForRestOfTheYear(PaymentPeriod type, LocalDate startDate) {
        return strategyFactory.getPayPeriodStrategy(type).schedulePayments(startDate);
    }

    private Double calculatePayAmountForPaymentDates(EmployeeResponse employee, PayrollDates payrollDate) {
        return strategyFactory.getSalaryCalculationStrategy(employee.getRateType()).calculateWage(employee, payrollDate);
    }

}
