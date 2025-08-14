package com.head4work.timecardsservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "time_devices")
public class TimePunchDevice extends AbstractBaseEntity {
    String companyId;
    String ein;
    String code;
    String accessKey;
}
