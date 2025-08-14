package com.head4work.timecardsservice.repositories;

import com.head4work.timecardsservice.entities.TimeCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeCardRepository extends JpaRepository<TimeCard, String> {

    TimeCard findByCompanyEmployeeIdAndDate(String companyEmployeeId, LocalDate date);

    Optional<List<TimeCard>> findAllByCompanyEmployeeId(String companyEmployeeId);

}
