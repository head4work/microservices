package com.head4work.companyservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CompanyEmployeesWithNamesAndTimeCardsDto {
    List<CompanyEmployeeWithNameDto> companyEmployees;
    List<TimeCardDto> timeCards;
}
