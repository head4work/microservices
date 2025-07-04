package com.head4work.companyservice.repositories;

import com.head4work.companyservice.entities.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, String> {
    boolean existsByEmployeeIdAndCompanyId(String employeeId, String companyId);

    @Modifying
    void deleteByCompanyIdAndEmployeeId(String companyId, String employeeId);
}
