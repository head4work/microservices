package com.head4work.timecardsservice.dto;

import com.head4work.timecardsservice.entities.TimeCard;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TimeCardUpdateDto {
    LocalDate date;
    private List<TimeCard.TimeSpan> timespans;
}
