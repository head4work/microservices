package com.head4work.companyservice.controller;

import com.head4work.companyservice.error.CustomResponseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.head4work.companyservice.util.AuthenticatedUser.getAuthenticatedUserId;


@RestController
@RequestMapping("/service/v1/company/")
public class CompanyController {

    @GetMapping("{id}")
    public ResponseEntity<String> getPet(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        String result = String.format("id: %s, name : bobby, breed: cannel, user_id : %s", id, userId);
        return ResponseEntity.ok(result);
    }
}
