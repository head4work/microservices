package com.head4work.employeeservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListEmployeesRequest {
    private List<String> employeesIds;
}
