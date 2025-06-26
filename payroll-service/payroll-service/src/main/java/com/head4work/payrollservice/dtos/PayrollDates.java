package com.head4work.payrollservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayrollDates {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate payrollDate;
}
