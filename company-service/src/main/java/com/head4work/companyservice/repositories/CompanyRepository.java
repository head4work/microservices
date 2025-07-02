package com.head4work.companyservice.repositories;

import com.head4work.companyservice.entities.Company;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findByIdAndUserId(String companyId, String userId);

    @Modifying
    @Transactional
    void deleteByIdAndUserId(String companyId, String userId);

    Optional<List<Company>> findAllByUserId(String userId);
}
