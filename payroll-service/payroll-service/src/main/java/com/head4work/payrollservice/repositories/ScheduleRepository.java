package com.head4work.payrollservice.repositories;

import com.head4work.payrollservice.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,String> {

}
