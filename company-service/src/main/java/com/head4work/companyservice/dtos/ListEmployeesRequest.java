package com.head4work.companyservice.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ListEmployeesRequest {
    private List<String> employeesIds;
}
