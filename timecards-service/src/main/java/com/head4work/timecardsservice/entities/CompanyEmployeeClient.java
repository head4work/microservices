package com.head4work.timecardsservice.entities;


import com.head4work.timecardsservice.config.FeignHeaderInterceptor;
import com.head4work.timecardsservice.dto.CompanyEmployeeDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "company-service", configuration = FeignHeaderInterceptor.class)
public interface CompanyEmployeeClient {
    @GetMapping("/service/v1/employees/{id}")
    CompanyEmployeeDto getEmployee(@PathVariable("id") String id);

    @PostMapping("/service/v1/employees/list")
    List<CompanyEmployeeDto> getEmployeesByIds(@RequestBody List<String> ids);
}
