package com.head4work.companyservice.repositories;

import com.head4work.companyservice.entities.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, String> {
    boolean existsByEmployeeIdAndCompanyIdAndUserId(String employeeId, String companyId, String userId);

    @Modifying
    void deleteByCompanyIdAndEmployeeIdAndUserId(String companyId, String employeeId, String userId);
}
