package com.head4work.timecardsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class TimeCardDto {
    String id;
    LocalDate date;
    Double hours;
    Double overtime;
}
