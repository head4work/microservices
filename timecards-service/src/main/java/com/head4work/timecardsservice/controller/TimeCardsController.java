package com.head4work.timecardsservice.controller;


import com.head4work.timecardsservice.dto.TimeCardDto;
import com.head4work.timecardsservice.dto.TimeCardUpdateDto;
import com.head4work.timecardsservice.entities.TimeCard;
import com.head4work.timecardsservice.entities.TimePunchDevice;
import com.head4work.timecardsservice.exceptions.CustomResponseException;
import com.head4work.timecardsservice.repositories.TimePunchDeviceRepository;
import com.head4work.timecardsservice.service.TimeCardService;
import com.head4work.timecardsservice.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;


@RequiredArgsConstructor
@RestController
@RequestMapping("/service/v1/timecards")
public class TimeCardsController {
    private final TimePunchDeviceRepository timePunchDeviceRepository;
    private static final Logger logger = LoggerFactory.getLogger(TimeCardsController.class);
    private final TimeCardService timeCardService;

    @PostMapping
    public ResponseEntity<TimeCard> createTimeCard(@RequestBody TimeCard timeCard) throws CustomResponseException {
        try {
            TimeCard createdTimeCard = timeCardService.create(timeCard);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdTimeCard.getId())
                    .toUri();
            return ResponseEntity.created(location).body(createdTimeCard);
        } catch (RuntimeException e) {
            throw new CustomResponseException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeCard> updateTimecard(@PathVariable String id, @RequestBody TimeCardUpdateDto uodatedCard) throws CustomResponseException {
        TimeCard updateTimeCard = timeCardService.updateById(uodatedCard, id);
        logger.info("Updating timecard with id: {}", id);
        return ResponseEntity.ok(updateTimeCard);
    }

    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeCard(@PathVariable String id) throws CustomResponseException {
        timeCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeCard> getTimeCardById(@PathVariable String id) throws CustomResponseException {
        TimeCard timeCard = timeCardService.getById(id);
        return ResponseEntity.ok(timeCard);
    }

    @GetMapping
    public ResponseEntity<List<TimeCardDto>> getAllTimeCards() throws CustomResponseException {
        logger.info("Retrieving all timecards");
        List<TimeCard> timeCards = timeCardService.getAllTimeCards();
        return ResponseEntity.ok(timeCardService.timeCardsToDtos(timeCards));
    }

    @PostMapping("/timecards_for_employees_list")
    public ResponseEntity<List<TimeCardDto>> getAllTimeCardsForEmployeesList(@RequestBody List<String> employeesIds) throws CustomResponseException {
        logger.info("Retrieving all timecards for employees list: {}", employeesIds);
        List<TimeCard> result = employeesIds.stream()
                .map(timeCardService::getAllByCompanyEmployee)
                .flatMap((Function<List<TimeCard>, Stream<TimeCard>>) Collection::stream)
                .toList();
        return ResponseEntity.ok(timeCardService.timeCardsToDtos(result));
    }

    @GetMapping("/{employeeId}/timecards_for_employee")
    public ResponseEntity<List<TimeCardDto>> getAllTimeCardsForEmployee(@PathVariable String employeeId) throws CustomResponseException {
        logger.info("Retrieving all timecards for employee: {}", employeeId);
        List<TimeCard> timeCards = timeCardService.getAllByCompanyEmployee(employeeId);
        List<TimeCardDto> timeCardDtos = timeCardService.timeCardsToDtos(timeCards);
        return ResponseEntity.ok(timeCardDtos);
    }

    @PostMapping("/device")
    public ResponseEntity<String> addDevice(@RequestBody TimePunchDevice punchDevice) throws CustomResponseException {
        try {
            //chek if punchDevice already exist
            TimePunchDevice byCompanyId = timePunchDeviceRepository.findByCompanyId(punchDevice.getCompanyId());
            if (byCompanyId != null) {
                punchDevice = byCompanyId;
            }
            // SET USER

            String key = CodeGenerator.generate6DigitCode();
            punchDevice.setCode(key);
            TimePunchDevice device = timePunchDeviceRepository.save(punchDevice);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(device.getId())
                    .toUri();
            return ResponseEntity.created(location).body(key);
        } catch (RuntimeException e) {
            throw new CustomResponseException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
