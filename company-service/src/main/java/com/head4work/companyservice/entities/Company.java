package com.head4work.companyservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "companies")
public class Company extends AbstractBaseEntity {
    @Column(name = "user_id", nullable = false)
    String userId;

    String name;
    String description;
    String address;
    String phone;

}
