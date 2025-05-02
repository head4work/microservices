package com.head4work.employeeservice.controllers;

import com.head4work.employeeservice.dto.EmployeeDto;
import com.head4work.employeeservice.dto.EmployeeResponse;
import com.head4work.employeeservice.entities.Employee;
import com.head4work.employeeservice.exceptions.CustomResponseException;
import com.head4work.employeeservice.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.head4work.employeeservice.util.AuthenticatedUser.getAuthenticatedUserId;


@RestController
@RequestMapping("/service/v1/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    //TODO REVISE THIS METHOD
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) throws CustomResponseException {

        try {
            String userId = getAuthenticatedUserId();
            // SET USER
            employee.setUserId(userId);
            Employee createdEmployee = employeeService.create(employee);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdEmployee.getId())
                    .toUri();
            return ResponseEntity.created(location).body(createdEmployee);
        } catch (RuntimeException e) {
            throw new CustomResponseException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @RequestBody EmployeeDto employeeDto) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        Employee updateEmployee = employeeService.updateEmployeeForUser(employeeDto, id, userId);
        logger.info("Updating employee with id: {}", id);
        return ResponseEntity.ok(updateEmployee);
    }

    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        employeeService.deleteForUser(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        Employee employee = employeeService.getByIdForUser(id, userId);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        logger.info("Retrieving all users");
        List<Employee> employees = employeeService.getAllByUserId(userId);
        return ResponseEntity.ok(employees);
    }

    @PostMapping("/list")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByIds(@RequestBody List<String> ids) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        return ResponseEntity.ok(employeeService.findEmployeesByIds(ids,userId));
    }


}
