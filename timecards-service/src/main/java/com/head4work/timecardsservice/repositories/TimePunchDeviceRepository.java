package com.head4work.timecardsservice.repositories;

import com.head4work.timecardsservice.entities.TimePunchDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimePunchDeviceRepository extends JpaRepository<TimePunchDevice, String> {
    Optional<TimePunchDevice> findByCode(String key);

    TimePunchDevice findByCompanyId(String companyId);

    Optional<TimePunchDevice> findByAccessKey(String accessKey);
}
