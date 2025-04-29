package com.head4work.payrollservice.entities;

import com.head4work.payrollservice.dtos.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "employee-service")
public interface EmployeeClient {
    @GetMapping("/service/v1/employees/{id}")
    EmployeeResponse getEmployee(@PathVariable("id") String id);
}
