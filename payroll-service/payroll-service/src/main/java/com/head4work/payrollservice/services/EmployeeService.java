package com.head4work.payrollservice.services;

import com.head4work.payrollservice.dtos.EmployeeResponse;
import com.head4work.payrollservice.entities.EmployeeClient;
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

    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeesByIdsFallback")
    public List<EmployeeResponse> getEmployeesByIds(List<String> ids) {
        logger.info("getEmployeesByIds called");
        return employeeClient.getEmployeesByIds(ids);
    }

    // Fallback method must match the original method's signature + Throwable
    public List<EmployeeResponse> getEmployeesByIdsFallback(List<String> ids, Throwable t) {
        // Return mock data on failure
        return List.of();
    }
}