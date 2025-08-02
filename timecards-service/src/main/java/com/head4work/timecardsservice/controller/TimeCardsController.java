package com.head4work.timecardsservice.controller;


import com.head4work.timecardsservice.dto.TimeCardDto;
import com.head4work.timecardsservice.dto.TimeCardUpdateDto;
import com.head4work.timecardsservice.entities.TimeCard;
import com.head4work.timecardsservice.exceptions.CustomResponseException;
import com.head4work.timecardsservice.service.TimeCardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.head4work.timecardsservice.util.AuthenticatedUser.getAuthenticatedUserId;


@RequiredArgsConstructor
@RestController
@RequestMapping("/service/v1/timecards")
public class TimeCardsController {

    private static final Logger logger = LoggerFactory.getLogger(TimeCardsController.class);
    private final TimeCardService timeCardService;

    @PostMapping
    public ResponseEntity<TimeCard> createTimeCard(@RequestBody TimeCard timeCard) throws CustomResponseException {
        try {
            String userId = getAuthenticatedUserId();
            // SET USER
            timeCard.setUserId(userId);
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
        String userId = getAuthenticatedUserId();
        TimeCard updateTimeCard = timeCardService.updateTimeCardForUser(uodatedCard, id, userId);
        logger.info("Updating timecard with id: {}", id);
        return ResponseEntity.ok(updateTimeCard);
    }

    //  @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeCard(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        timeCardService.deleteForUser(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeCard> getTimeCardById(@PathVariable String id) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        TimeCard timeCard = timeCardService.getByIdForUser(id, userId);
        return ResponseEntity.ok(timeCard);
    }

    @GetMapping
    public ResponseEntity<List<TimeCardDto>> getAllTimeCards() throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        logger.info("Retrieving all timecards for user: {}", userId);
        List<TimeCard> timeCards = timeCardService.getAllByUserId(userId);
        return ResponseEntity.ok(timeCardService.timeCardsToDtos(timeCards));
    }

    @GetMapping("/{employeeId}/timecards_for_employee")
    public ResponseEntity<List<TimeCardDto>> getAllTimeCardsForEmployee(@PathVariable String employeeId) throws CustomResponseException {
        String userId = getAuthenticatedUserId();
        logger.info("Retrieving all timecards for employee: {}", userId);
        List<TimeCard> timeCards = timeCardService.getAllByUserAndEmployee(userId, employeeId);
        List<TimeCardDto> timeCardDtos = timeCardService.timeCardsToDtos(timeCards);
        return ResponseEntity.ok(timeCardDtos);
    }

}
