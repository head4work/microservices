package com.head4work.payrollservice.dtos;

import lombok.Data;

@Data
public class EmployeeResponse {
    private String id;
    private String name;
    private String rateType;
    private double salaryAmount;
}