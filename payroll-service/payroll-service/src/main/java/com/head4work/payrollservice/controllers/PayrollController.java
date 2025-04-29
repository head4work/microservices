package com.head4work.payrollservice.controllers;

import com.head4work.payrollservice.dtos.EmployeeResponse;
import com.head4work.payrollservice.entities.EmployeeClient;
import com.head4work.payrollservice.entities.Payroll;
import com.head4work.payrollservice.entities.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/service/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final EmployeeClient employeeClient;

    @GetMapping
    public List<Payroll> calculatePayrollsForSchedule(Schedule schedule) {
        List<Payroll> payrolls = new ArrayList<>();
        schedule.getEmployeeIds().forEach(employeeId -> {
            Payroll payroll = new Payroll();
            EmployeeResponse employee = employeeClient.getEmployee(employeeId);
            Payroll.builder()

                    .employeeId(employeeId)
                    .build();
        });
        List<Payroll> schedules = getPayrollSchedule(employee, currentSalary.getPaymentPeriod());
        return Collections.emptyList();
    }

}
