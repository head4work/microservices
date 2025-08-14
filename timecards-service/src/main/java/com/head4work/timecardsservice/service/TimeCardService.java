package com.head4work.timecardsservice.service;

import com.head4work.timecardsservice.dto.TimeCardDto;
import com.head4work.timecardsservice.dto.TimeCardUpdateDto;
import com.head4work.timecardsservice.entities.TimeCard;
import com.head4work.timecardsservice.exceptions.CustomResponseException;
import com.head4work.timecardsservice.repositories.TimeCardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TimeCardService {
    Logger logger = LoggerFactory.getLogger(TimeCardService.class);
    private final TimeCardRepository timeCardRepository;
    private final ModelMapper modelMapper;

    public TimeCard create(TimeCard timeCard) {
        return timeCardRepository.save(timeCard);
    }

    public TimeCard updateById(TimeCardUpdateDto updatedCard, String id) {
        TimeCard timeCard = getById(id);
        modelMapper.map(updatedCard, timeCard);
        return timeCardRepository.save(timeCard);
    }

    public void deleteById(String id) {
        timeCardRepository.deleteById(id);
    }

    public TimeCard getById(String id) {
        return timeCardRepository.findById(id)
                .orElseThrow(() -> new CustomResponseException("No timecard found", HttpStatus.BAD_REQUEST));
    }

    public List<TimeCard> getAllTimeCards() {
        return timeCardRepository.findAll();
    }

    public List<TimeCard> getAllByCompanyEmployee(String employeeId) {
        logger.info("get all timecards for employee id {}", employeeId);
        return timeCardRepository.findAllByCompanyEmployeeId(employeeId)
                .orElseThrow(() ->
                        new CustomResponseException("There are no time cards for this employee", HttpStatus.BAD_REQUEST));
    }

    public List<TimeCardDto> timeCardsToDtos(List<TimeCard> timeCards) {
        List<TimeCardDto> timeCardDtos = new ArrayList<>();
        timeCards.forEach(timeCard -> {
            double hours = 0.0;
            for (TimeCard.TimeSpan timeSpan : timeCard.getTimespans()) {
                if (timeSpan.getStart() != null && timeSpan.getEnd() != null) {
                    hours += Duration.between(timeSpan.getStart(), timeSpan.getEnd()).toMinutes() / 60.0;
                }
            }
            double overtime = hours > 8.0 ? hours - 8.0 : 0.0;
            timeCardDtos.add(
                    TimeCardDto.builder()
                            .id(timeCard.getId())
                            .companyEmployeeId(timeCard.getCompanyEmployeeId())
                            .date(timeCard.getDate())
                            .hours(hours)
                            .overtime(overtime)
                            .build()
            );

        });
        return timeCardDtos;
    }

}
