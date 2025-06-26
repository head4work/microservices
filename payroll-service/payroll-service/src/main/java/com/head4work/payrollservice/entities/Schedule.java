package com.head4work.payrollservice.entities;

import com.head4work.payrollservice.enums.PaymentPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedules")
public class Schedule extends AbstractBaseEntity {
    LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type")
    private PaymentPeriod type;

    @ElementCollection
    @CollectionTable(name = "schedule_employees", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "employee_id")
    private List<String> employeeIds;
}
