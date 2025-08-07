package com.head4work.companyservice.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class TimeCardDto {
    String id;
    String companyEmployeeId;
    LocalDate date;
    Double hours;
    Double overtime;
}
