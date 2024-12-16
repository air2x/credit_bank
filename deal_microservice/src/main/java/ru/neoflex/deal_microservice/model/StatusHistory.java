package ru.neoflex.deal_microservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.ApplicationStatus;
import ru.neoflex.enums.ChangeType;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusHistory {
    private ApplicationStatus status;
    private LocalDate time;
    private ChangeType changeType;
}
