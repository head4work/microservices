package com.head4work.timecardsservice.service;

import com.head4work.timecardsservice.dto.TimeCardDto;
import com.head4work.timecardsservice.entities.TimeCard;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TimeCardService {
    Logger logger = LoggerFactory.getLogger(TimeCardService.class);


    public TimeCard create(TimeCard timeCard) {
        return null;
    }

    public TimeCard updateTimeCardForUser(TimeCardDto timeCardDto, String id, String userId) {
        return null;
    }

    public void deleteForUser(String id, String userId) {

    }

    public TimeCard getByIdForUser(String id, String userId) {
        return null;
    }

    public List<TimeCard> getAllByUserId(String userId) {
        return null;
    }
}
