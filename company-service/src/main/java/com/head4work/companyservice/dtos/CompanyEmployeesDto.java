package com.head4work.companyservice.dtos;

import com.head4work.companyservice.entities.CompanyEmployee;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CompanyEmployeesDto {
    List<CompanyEmployee> companyEmployees;
    List<EmployeeResponse> employees;
}
