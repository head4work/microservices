package com.head4work.companyservice.services;


import com.head4work.companyservice.dtos.CompanyDto;
import com.head4work.companyservice.dtos.EmployeeResponse;
import com.head4work.companyservice.entities.Company;
import com.head4work.companyservice.entities.EmployeeClient;
import com.head4work.companyservice.exceptions.CompanyNotFoundException;
import com.head4work.companyservice.repositories.CompanyRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CompanyService {
    Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private final EmployeeClient employeeClient;
    private final CompanyRepository companyRepository;
    private final ModelMapper modelMapper;

    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeeBackUp")
    public EmployeeResponse getEmployee(String id) {
        logger.info("getEmployee called");
        return employeeClient.getEmployee(id);
    }

    // fallback for get employee
    public EmployeeResponse getEmployeeBackUp(String id, Throwable t) {
        return EmployeeResponse.builder()
                .id("mock")
                .firstName("mock")
                .lastName("mock")
                .build();
    }

    @CircuitBreaker(name = "employeeService", fallbackMethod = "getEmployeesByIdsFallback")
    public List<EmployeeResponse> getEmployeesByIds(List<String> ids) {
        logger.info("getEmployeesByIds called");
        return employeeClient.getEmployeesByIds(ids);
    }

    // Fallback method must match the original method's signature + Throwable
    public List<EmployeeResponse> getEmployeesByIdsFallback(List<String> ids, Throwable t) {
        // Return mock data on failure
        return List.of(EmployeeResponse.builder()
                .id("mock")
                .firstName("mock")
                .lastName("mock")
                .build());
    }

    public Company create(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompanyForUser(CompanyDto companyDto, String id, String userId) {
        Company company = getByIdForUser(id, userId);
        modelMapper.map(companyDto, company);
        return companyRepository.save(company);
    }

    public Company getByIdForUser(String companyId, String userId) {
        return companyRepository.findByIdAndUserId(companyId, userId).orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    public void deleteForUser(String id, String userId) {
        companyRepository.deleteByIdAndUserId(id, userId);
    }

    public List<Company> getAllByUserId(String userId) {
        return companyRepository.findAllByUserId(userId).orElseThrow(() -> new CompanyNotFoundException(userId));
    }
}