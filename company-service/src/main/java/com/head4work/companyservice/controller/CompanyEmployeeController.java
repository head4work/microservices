package com.head4work.companyservice.controller;

import com.head4work.companyservice.dtos.CompanyEmployeeRequest;
import com.head4work.companyservice.entities.CompanyEmployee;
import com.head4work.companyservice.exceptions.CustomResponseException;
import com.head4work.companyservice.repositories.CompanyEmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.head4work.companyservice.util.AuthenticatedUser.getAuthenticatedUserId;

@RequiredArgsConstructor
@RestController
@RequestMapping("/service/v1/company")
public class CompanyEmployeeController {
    Logger logger = LoggerFactory.getLogger(CompanyEmployeeController.class);
    private final CompanyEmployeeRepository companyEmployeeRepository;

    @PostMapping("/{companyId}/employee")
    public ResponseEntity<Void> assignEmployee(@PathVariable String companyId,
                                               @RequestBody CompanyEmployeeRequest request)
            throws CustomResponseException {

        String employeeId = request.getEmployeeId();
        String userId = getAuthenticatedUserId();

        if (!companyEmployeeRepository.existsByEmployeeIdAndCompanyIdAndUserId(employeeId, companyId, userId)) {
            CompanyEmployee companyEmployee = CompanyEmployee.builder()
                    .companyId(companyId)
                    .employeeId(employeeId)
                    .userId(userId)
                    .build();
            companyEmployeeRepository.save(companyEmployee);
        } else {
            throw new CustomResponseException("Employee already assigned to this company", HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{companyId}/employee")
    public ResponseEntity<Void> removeEmployeeFromCompany(@PathVariable String companyId,
                                                          @RequestParam String employeeId
    ) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        companyEmployeeRepository.deleteByCompanyIdAndEmployeeIdAndUserId(companyId, employeeId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{companyId}/employee")
    public ResponseEntity<List<CompanyEmployee>> getAllCompanyEmployees(@PathVariable String companyId) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        logger.info("Retrieving all employees for company: {}", companyId);
        List<CompanyEmployee> employees = companyEmployeeRepository.getAllByCompanyIdAndUserId(companyId, userId);
        return ResponseEntity.ok(employees);
    }
}
