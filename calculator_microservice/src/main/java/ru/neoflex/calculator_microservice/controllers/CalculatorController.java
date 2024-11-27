package ru.neoflex.calculator_microservice.controllers;

import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger logger = LogManager.getLogger(CalculatorController.class);

    @Autowired
    public CalculatorController(CalculateService calculateService, LoanOffersService loanOffersService) {
        this.calculateService = calculateService;
        this.loanOffersService = loanOffersService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDto> offers(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto) {
        logger.info("Loan statement request has been received");
        return loanOffersService.getLoanOffersDto(loanStatementRequestDto);
    }

    @PostMapping("/calc")
    public CreditDto offers(@RequestBody @Valid ScoringDataDto scoringDataDto) {
        logger.info("The data for the scoring has been received");
        return calculateService.getCreditDto(scoringDataDto);
    }
}
