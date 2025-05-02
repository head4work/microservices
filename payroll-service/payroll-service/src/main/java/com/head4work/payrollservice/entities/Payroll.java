package com.head4work.payrollservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payrolls")
public class Payroll extends AbstractBaseEntity {
    @Column(name = "pay_date")
    LocalDate payDate;

    @Column(name = "start_period")
    LocalDate startPeriod;

    @Column(name = "end_period")
    LocalDate endPeriod;

    @Column(name = "employee_id")
    String employeeId;

    @Column(name = "employee_rate")
    Double employeeRate;

    @Column(name = "payment_amount")
    Double paymentAmount;

    @Column(name = "payment_rate")
    Double paymentRate;

}
