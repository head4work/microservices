package com.head4work.payrollservice.entities;

import com.head4work.payrollservice.config.FeignHeaderInterceptor;
import com.head4work.payrollservice.dtos.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "employee-service", configuration = FeignHeaderInterceptor.class)
public interface EmployeeClient {
    @GetMapping("/service/v1/employees/{id}")
    EmployeeResponse getEmployee(@PathVariable("id") String id);

    @PostMapping("/service/v1/employees/list")
    List<EmployeeResponse> getEmployeesByIds(@RequestBody List<String> ids);
}
