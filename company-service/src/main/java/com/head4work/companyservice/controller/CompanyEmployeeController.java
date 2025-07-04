package com.head4work.companyservice.controller;

import com.head4work.companyservice.dtos.CompanyEmployeeRequest;
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

    @DeleteMapping("/{companyId}/employees/{employeeId}")
    public ResponseEntity<Void> removeEmployeeFromCompany(@PathVariable String companyId,
                                               @RequestBody CompanyEmployeeRequest request
    ) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        companyEmployeeRepository.deleteByCompanyIdAndEmployeeIdAndUserId(companyId, request.getEmployeeId(), userId);
        return ResponseEntity.ok().build();
    }
}
