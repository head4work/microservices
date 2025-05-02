package com.head4work.employeeservice.dto;

import com.head4work.employeeservice.enums.RateType;
import lombok.Data;

@Data
public class EmployeeResponse {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private RateType rateType;
    private double rate;
}