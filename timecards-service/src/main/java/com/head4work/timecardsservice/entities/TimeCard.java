package com.head4work.timecardsservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "time_cards")
public class TimeCard extends AbstractBaseEntity {
    LocalDate date;
    String userId;
    String companyEmployeeId;

    @ElementCollection
    @CollectionTable(name = "timecard_timespans", joinColumns = @JoinColumn(name = "timecard_id"))
    private List<TimeSpan> timespans = new ArrayList<>();

    @Embeddable
    @Data
    private static class TimeSpan {
        LocalDateTime start;
        LocalDateTime end;
    }
}
