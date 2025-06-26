package com.head4work.payrollservice.services;

import com.head4work.payrollservice.entities.Schedule;
import com.head4work.payrollservice.exceptions.CustomResponseException;
import com.head4work.payrollservice.repositories.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public Schedule getSchedule(String id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new CustomResponseException(String.format("Schedule with id:%s not found", id), HttpStatus.BAD_REQUEST));
    }

    public Schedule saveSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

}
