package com.head4work.companyservice.repositories;

import com.head4work.companyservice.entities.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, String> {
    boolean existsByEmployeeIdAndCompanyIdAndUserId(String employeeId, String companyId, String userId);

    @Modifying
    @Transactional
    void deleteByCompanyIdAndEmployeeIdAndUserId(String companyId, String employeeId, String userId);

    List<CompanyEmployee> getAllByCompanyIdAndUserId(String companyId, String userId);
}
