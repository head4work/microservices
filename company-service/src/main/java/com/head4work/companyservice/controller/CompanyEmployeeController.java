package com.head4work.companyservice.controller;

import com.head4work.companyservice.dtos.CompanyEmployeeRequest;
import com.head4work.companyservice.dtos.CompanyEmployeesDto;
import com.head4work.companyservice.dtos.EmployeeResponse;
import com.head4work.companyservice.entities.CompanyEmployee;
import com.head4work.companyservice.exceptions.CustomResponseException;
import com.head4work.companyservice.repositories.CompanyEmployeeRepository;
import com.head4work.companyservice.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import static com.head4work.companyservice.util.AuthenticatedUser.getAuthenticatedUserId;

@RequiredArgsConstructor
@RestController
@RequestMapping("/service/v1/company")
public class CompanyEmployeeController {
    Logger logger = LoggerFactory.getLogger(CompanyEmployeeController.class);
    private final CompanyEmployeeRepository companyEmployeeRepository;
    private final CompanyService companyService;
    private final Executor applicationTaskExecutor;

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

    @GetMapping("/{companyId}/company_employees")
    public ResponseEntity<CompanyEmployeesDto> getAllCompanyEmployeesAndEmployees(@PathVariable String companyId) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
//        List<CompanyEmployee> companyEmployees = companyEmployeeRepository.getAllByCompanyIdAndUserId(companyId, userId);
//        List<EmployeeResponse> employees = companyService.getAllEmployees();

        try {
            CompletableFuture<List<CompanyEmployee>> companyEmployeesFuture = CompletableFuture.supplyAsync(
                    () -> companyEmployeeRepository.getAllByCompanyIdAndUserId(companyId, userId)
                    // applicationTaskExecutor // Specify the executor to use
            );
            CompletableFuture<List<EmployeeResponse>> employeesFuture = CompletableFuture.supplyAsync(

                    companyService::getAllEmployees, // Using a method reference for conciseness
                    applicationTaskExecutor // Specify the executor to use
            );
            CompletableFuture.allOf(companyEmployeesFuture, employeesFuture).join();
            List<CompanyEmployee> companyEmployees = companyEmployeesFuture.get();
            List<EmployeeResponse> employees = employeesFuture.get();

            CompanyEmployeesDto companyEmployeesDto = CompanyEmployeesDto.builder()
                    .employees(employees)
                    .companyEmployees(companyEmployees)
                    .build();
            return ResponseEntity.ok(companyEmployeesDto);
        } catch (CompletionException e) {
            // Handle exceptions that occurred within the CompletableFuture tasks.
            // CompletionException wraps the original exception (e.g., from network call, DB error).
            Throwable cause = e.getCause(); // Get the root cause of the exception
            logger.error("Error during parallel data fetch: {}", cause.getMessage());
            // Re-throw a custom exception or return an appropriate error response
            throw new CustomResponseException("Failed to retrieve company employees or all employees: " + cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Catch any other unexpected exceptions (e.g., InterruptedException if thread is interrupted)
            logger.error("An unexpected error occurred during data fetch: {}", e.getMessage());
            throw new CustomResponseException("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
