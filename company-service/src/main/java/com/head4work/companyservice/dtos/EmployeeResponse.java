package com.head4work.companyservice.dtos;

import com.head4work.companyservice.enums.RateType;
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