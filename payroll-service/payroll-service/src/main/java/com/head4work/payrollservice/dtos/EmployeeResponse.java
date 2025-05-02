package com.head4work.payrollservice.dtos;

import com.head4work.payrollservice.enums.RateType;
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