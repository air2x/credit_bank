package ru.neoflex.calculator_microservice.services;

import org.springframework.stereotype.Service;
import ru.neoflex.calculator_microservice.dto.CreditDto;
import ru.neoflex.calculator_microservice.dto.ScoringDataDto;

@Service
public class CalculateService {
    public CreditDto getCreditDto(ScoringDataDto scoringDataDto) {
        CreditDto creditDto = new CreditDto();
        return creditDto;
    }
}
