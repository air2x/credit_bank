package ru.neoflex.calculator_microservice.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.calculator_microservice.dto.CreditDto;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;
import ru.neoflex.calculator_microservice.dto.ScoringDataDto;
import ru.neoflex.calculator_microservice.services.CalculateService;
import ru.neoflex.calculator_microservice.services.LoanOffersService;

import java.util.List;

@RestController("/calculator")
public class CalculatorController {

    private final CalculateService calculateService;
    private final LoanOffersService loanOffersService;

    @Autowired
    public CalculatorController(CalculateService calculateService, LoanOffersService loanOffersService) {
        this.calculateService = calculateService;
        this.loanOffersService = loanOffersService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDto> offers(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto) {
        return loanOffersService.getLoanOffersDto(loanStatementRequestDto);
    }

    @PostMapping("/calc")
    public CreditDto offers(@RequestBody @Valid ScoringDataDto scoringDataDto) {
        return calculateService.getCreditDto(scoringDataDto);
    }
}
