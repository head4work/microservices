package com.head4work.companyservice.controller;

import com.head4work.companyservice.dtos.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/{companyId}/employees_with_names")
    public ResponseEntity<CompanyEmployeesWithNamesAndTimeCardsDto> getAllCompanyEmployeesWithNames(
            @PathVariable String companyId) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        logger.info("Retrieving all employees with names and timecards for company: {}", companyId);

        List<CompanyEmployee> companyEmployees =
                companyEmployeeRepository.getAllByCompanyIdAndUserId(companyId, userId); // sync call
        try {
            CompletableFuture<List<EmployeeResponse>> employeesFuture =
                    CompletableFuture.supplyAsync(
                            companyService::getAllEmployees,
                            applicationTaskExecutor
                    );

            CompletableFuture<List<TimeCardDto>> timeCardsFuture =
                    CompletableFuture.supplyAsync(
                            () -> {
                                List<String> ids = companyEmployees.stream()
                                        .map(CompanyEmployee::getId)
                                        .toList();
                                return companyService.getAllTimeCardsForEmployeesList(ids);
                            },
                            applicationTaskExecutor
                    );

            CompletableFuture.allOf(employeesFuture, timeCardsFuture).join();
            List<EmployeeResponse> employees = employeesFuture.get();
            List<TimeCardDto> timeCards = timeCardsFuture.get();

            Map<String, String> map = new HashMap<>();
            for (EmployeeResponse e : employees) {
                map.put(e.getId(), e.getFirstName() + " " + e.getLastName());
            }

            List<CompanyEmployeeWithNameDto> companyEmployeeWithNamesDto = new ArrayList<>();
            for (CompanyEmployee companyEmployee : companyEmployees) {
                companyEmployeeWithNamesDto.add(CompanyEmployeeWithNameDto.builder()
                        .id(companyEmployee.getId())
                        .name(map.get(companyEmployee.getEmployeeId()))
                        .build());
            }

            return ResponseEntity.ok(CompanyEmployeesWithNamesAndTimeCardsDto.builder()
                    .timeCards(timeCards)
                    .companyEmployees(companyEmployeeWithNamesDto)
                    .build());

        } catch (CompletionException e1) {
            // Handle exceptions that occurred within the CompletableFuture tasks.
            // CompletionException wraps the original exception (e.g., from network call, DB error).
            Throwable cause = e1.getCause(); // Get the root cause of the exception
            logger.error("Error during parallel data fetch: {}", cause.getMessage());
            // Re-throw a custom exception or return an appropriate error response
            throw new CustomResponseException("Failed to retrieve company employees or all employees: " + cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e1) {
            // Catch any other unexpected exceptions (e.g., InterruptedException if thread is interrupted)
            logger.error("An unexpected error occurred during data fetch: {}", e1.getMessage());
            throw new CustomResponseException("An unexpected error occurred: " + e1.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/{companyId}/company_employees")
    public ResponseEntity<CompanyEmployeesDto> getAllCompanyEmployeesAndEmployees(@PathVariable String companyId) throws CustomResponseException {
        String userId = getAuthenticatedUserId();

        List<CompanyEmployee> companyEmployees = companyEmployeeRepository.getAllByCompanyIdAndUserId(companyId, userId);
        try {
            CompletableFuture<List<EmployeeResponse>> employeesFuture = CompletableFuture.supplyAsync(
                    companyService::getAllEmployees, // Using a method reference for conciseness
                    applicationTaskExecutor // Specify the executor to use
            );
            List<EmployeeResponse> employees = employeesFuture.get();

            return ResponseEntity.ok(CompanyEmployeesDto.builder()
                    .employees(employees)
                    .companyEmployees(companyEmployees)
                    .build());

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

    private record CompanyEmployeeFutures(CompletableFuture<List<CompanyEmployee>> companyEmployeesFuture,
                                          CompletableFuture<List<EmployeeResponse>> employeesFuture) {
    }

}
