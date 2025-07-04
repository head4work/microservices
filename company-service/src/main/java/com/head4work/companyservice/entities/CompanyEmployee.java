package com.head4work.companyservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company_employees")
public class CompanyEmployee extends AbstractBaseEntity {

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
