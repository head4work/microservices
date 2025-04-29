package com.head4work.payrollservice.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    public boolean isNew() {
        return this.id == null;
    }
}
