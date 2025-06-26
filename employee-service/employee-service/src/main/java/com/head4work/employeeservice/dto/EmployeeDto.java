package com.head4work.employeeservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDto {

    @NotEmpty(message = "Username mustn't be empty")
    @Size(min = 2, max = 100, message = "Username should be 2-100 characters")
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private Integer phone;
    private LocalDate hired;

}
