package com.head4work.companyservice.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompanyEmployeeWithNameDto {
    String id;
    String name;
}
