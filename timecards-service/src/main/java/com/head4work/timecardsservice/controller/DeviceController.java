package com.head4work.timecardsservice.controller;

import com.head4work.timecardsservice.dto.RegisterDeviceRequest;
import com.head4work.timecardsservice.entities.TimeCard;
import com.head4work.timecardsservice.entities.TimePunch;
import com.head4work.timecardsservice.entities.TimePunchDevice;
import com.head4work.timecardsservice.exceptions.CustomResponseException;
import com.head4work.timecardsservice.repositories.TimeCardRepository;
import com.head4work.timecardsservice.repositories.TimePunchDeviceRepository;
import com.head4work.timecardsservice.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/public/v1/device")
public class DeviceController {
    private final TimePunchDeviceRepository repository;
    private final TimeCardRepository timeCardRepository;


    @PostMapping("/register")
    public ResponseEntity<String> registerDevice(@RequestBody RegisterDeviceRequest register) throws CustomResponseException {
        TimePunchDevice timePunchDevice = repository.findByCode(register.getCode())
                .orElseThrow(() -> new CustomResponseException("key is invalid", HttpStatus.BAD_REQUEST));
        String token = CodeGenerator.generateToken(32);
        timePunchDevice.setAccessKey(token);
        timePunchDevice.setCode(null);
        repository.save(timePunchDevice);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/punch-clock")
    public ResponseEntity<TimeCard> timePunch(
            @RequestBody TimePunch punch, @RequestHeader("DeviceToken") String deviceToken)
            throws CustomResponseException {
        repository.findByAccessKey(deviceToken).orElseThrow(
                () -> new CustomResponseException("token is invalid", HttpStatus.UNAUTHORIZED));
        LocalDate date = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        TimeCard timeCard = timeCardRepository.findByCompanyEmployeeIdAndDate(punch.getCompanyEmployeeId(), date);
        if (timeCard == null) {
            timeCard = TimeCard.builder()
                    .date(date)
                    .companyEmployeeId(punch.getCompanyEmployeeId())
                    .timespans(List.of(new TimeCard.TimeSpan(now)))
                    .build();
        } else {
            TimeCard.TimeSpan last = timeCard.getTimespans().getLast();
            if (last.getEnd() == null) {
                last.setEnd(now);
            } else {
                timeCard.getTimespans().add(new TimeCard.TimeSpan(now));
            }
        }
        timeCardRepository.save(timeCard);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(timeCard.getId())
                .toUri();
        return ResponseEntity.created(location).body(timeCard);

    }
}
