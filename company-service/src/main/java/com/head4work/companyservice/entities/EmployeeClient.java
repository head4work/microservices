package com.head4work.companyservice.entities;


import com.head4work.companyservice.config.FeignHeaderPropagationConfig;
import com.head4work.companyservice.dtos.EmployeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "employee-service", configuration = FeignHeaderPropagationConfig.class)
public interface EmployeeClient {
    @GetMapping("/service/v1/employees/{id}")
    EmployeeResponse getEmployee(@PathVariable("id") String id);

    @GetMapping("/service/v1/employees")
    List<EmployeeResponse> getAllEmployees();
}
