package ru.neoflex.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.neoflex.enums.ApplicationStatus;
import ru.neoflex.enums.ChangeType;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatementStatusHistoryDto {

    private ApplicationStatus status;
    private LocalDateTime time;
    private ChangeType changeType;
}
