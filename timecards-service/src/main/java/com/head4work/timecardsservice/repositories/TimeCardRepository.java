package com.head4work.timecardsservice.repositories;

import com.head4work.timecardsservice.entities.TimeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeCardRepository extends JpaRepository<TimeCard, String> {
    Optional<TimeCard> findById(String id);

    Optional<List<TimeCard>> findAllByUserIdAndCompanyEmployeeId(String userId, String companyEmployeeId);

    Optional<TimeCard> findByIdAndUserId(String id, String userId);

    Optional<List<TimeCard>> findAllByUserId(String userId);
}
