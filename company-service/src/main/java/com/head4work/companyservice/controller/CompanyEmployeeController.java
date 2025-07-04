package com.head4work.companyservice.controller;

import com.head4work.companyservice.dtos.CompanyEmployeeRequest;
import com.head4work.companyservice.dtos.EmployeeResponse;
import com.head4work.companyservice.entities.Company;
import com.head4work.companyservice.entities.CompanyEmployee;
import com.head4work.companyservice.error.CustomResponseException;
import com.head4work.companyservice.repositories.CompanyEmployeeRepository;
import com.head4work.companyservice.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.head4work.companyservice.util.AuthenticatedUser.getAuthenticatedUserId;

@RequiredArgsConstructor
@RestController
@RequestMapping("/service/v1/company")
public class CompanyEmployeeController {
    private final CompanyService companyService;
    private final CompanyEmployeeRepository companyEmployeeRepository;

    //TODO check user owns entities
    @PostMapping("/{companyId}/employee")
    public ResponseEntity<Void> assignEmployee(@PathVariable String companyId,
                                               @RequestBody CompanyEmployeeRequest request)
            throws CustomResponseException {

        String employeeId = request.getEmployeeId();
        String userId = getAuthenticatedUserId();
        Company company = companyService.getByIdForUser(companyId, userId);
        EmployeeResponse employee = companyService.getEmployee(employeeId);
        if (!companyEmployeeRepository.existsByEmployeeIdAndCompanyId(employeeId, companyId)) {
            CompanyEmployee companyEmployee = CompanyEmployee.builder()
                    .companyId(companyId)
                    .employeeId(employeeId)
                    .build();
            companyEmployeeRepository.save(companyEmployee);
        } else {
            throw new CustomResponseException("Employee already assigned to company %s".formatted(company.getName()), HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok().build();

    }

    @DeleteMapping("/{companyId}/employees/{employeeId}")
    public ResponseEntity<Void> removeEmployee(@PathVariable String companyId,
                                               @RequestBody CompanyEmployeeRequest request
    ) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        Company company = companyService.getByIdForUser(companyId, userId);

        companyEmployeeRepository.deleteByCompanyIdAndEmployeeId(companyId, request.getEmployeeId());
        return ResponseEntity.ok().build();
    }
}
