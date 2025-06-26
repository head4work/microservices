package com.example.authservice.repositories;


import com.example.authservice.entities.User;
import com.example.authservice.exceptions.CustomJwtException;
import com.example.authservice.exceptions.UserNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Data
@RequiredArgsConstructor
@Repository
public class JpaUserRepository {
    private final UserRepository repository;

    public User createUser(User user) {
        try {
            return repository.save(user);
        } catch (Exception e) {
            if (e.getMessage().contains("duplicate")) {
                throw new CustomJwtException("This email already exist", HttpStatus.BAD_REQUEST);
            }
            throw e;
        }
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public User getById(String id) {
        return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }
}
