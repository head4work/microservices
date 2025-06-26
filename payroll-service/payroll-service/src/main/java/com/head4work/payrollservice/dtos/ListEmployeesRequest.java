package com.head4work.payrollservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ListEmployeesRequest {
    private List<String> employeesIds;
}
