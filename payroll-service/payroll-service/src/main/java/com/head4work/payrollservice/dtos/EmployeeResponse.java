package com.head4work.payrollservice.dtos;

import com.head4work.payrollservice.enums.RateType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeResponse {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private RateType rateType;
    private double rate;
}