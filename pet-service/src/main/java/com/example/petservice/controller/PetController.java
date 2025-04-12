package com.example.petservice.controller;

import com.example.petservice.error.CustomResponseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.petservice.util.AuthenticatedUser.getAuthenticatedUserId;

@RestController
@RequestMapping("/service/v1/pets/")
public class PetController {

    @GetMapping("{id}")
    public ResponseEntity<String> getPet(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        String result = String.format("id: %s, name : bobby, breed: cannel, user_id : %s", id, userId);
        return ResponseEntity.ok(result);
    }
}
