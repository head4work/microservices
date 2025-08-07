package com.head4work.companyservice.entities;

import com.head4work.companyservice.config.FeignHeaderPropagationConfig;
import com.head4work.companyservice.dtos.TimeCardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "timecards-service", configuration = FeignHeaderPropagationConfig.class)
public interface TimeCardsClient {
    @PostMapping("/service/v1/timecards/timecards_for_employees_list")
    List<TimeCardDto> getAllTimeCardsForEmployeesList(@RequestBody List<String> employeesList);
}
