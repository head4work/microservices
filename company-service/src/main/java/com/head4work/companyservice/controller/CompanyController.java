package com.head4work.companyservice.controller;

import com.head4work.companyservice.dtos.CompanyDto;
import com.head4work.companyservice.entities.Company;
import com.head4work.companyservice.error.CustomResponseException;
import com.head4work.companyservice.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.head4work.companyservice.util.AuthenticatedUser.getAuthenticatedUserId;

@RequiredArgsConstructor
@RestController
@RequestMapping("/service/v1/company")
public class CompanyController {
    private final CompanyService companyService;
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @PostMapping
    public ResponseEntity<Company> createEmployee(@RequestBody Company company) throws CustomResponseException {

        try {
            String userId = getAuthenticatedUserId();
            // SET USER
            company.setUserId(userId);
            Company createdCompany = companyService.create(company);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdCompany.getId())
                    .toUri();
            return ResponseEntity.created(location).body(createdCompany);
        } catch (RuntimeException e) {
            throw new CustomResponseException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable String id, @RequestBody CompanyDto companyDto) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        Company updateCompany = companyService.updateCompanyForUser(companyDto, id, userId);
        logger.info("Updating employee with id: {}", id);
        return ResponseEntity.ok(updateCompany);
    }

    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        companyService.deleteForUser(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        Company company = companyService.getByIdForUser(id, userId);
        return ResponseEntity.ok(company);
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAllUserCompanies() throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        logger.info("Retrieving all companies for user: {}", userId);
        List<Company> employees = companyService.getAllByUserId(userId);
        return ResponseEntity.ok(employees);
    }


}
