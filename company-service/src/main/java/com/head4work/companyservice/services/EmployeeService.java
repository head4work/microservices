package com.head4work.companyservice.services;


import com.head4work.companyservice.dtos.EmployeeResponse;
import com.head4work.companyservice.entities.EmployeeClient;
import com.head4work.companyservice.exceptions.EmptyCompanyException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeClient employeeClient;

    public EmployeeService(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "getAllCompanyEmployeesBackUp")
    public List<EmployeeResponse> getAllCompanyEmployees(String id) {
        logger.info("getAllCompanyEmployees called");
        return employeeClient.getAllCompanyEmployees(id);
    }

    // Fallback method must match the original method's signature + Throwable
    public List<EmployeeResponse> getAllCompanyEmployeesBackUp(String id, Throwable t) {
        // Return mock data on failure
        if (((FeignException.BadRequest) t).contentUTF8().contains("No employees assigned")) {
            throw new EmptyCompanyException();
        }
        return List.of(EmployeeResponse.builder()
                .id("mock")
                .firstName("mock")
                .lastName("mock")
                .build());
    }
}