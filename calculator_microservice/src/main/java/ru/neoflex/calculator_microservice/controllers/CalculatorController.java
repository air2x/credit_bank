package ru.neoflex.calculator_microservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.calculator_microservice.dto.CreditDto;
import ru.neoflex.calculator_microservice.dto.LoanOfferDto;
import ru.neoflex.calculator_microservice.dto.LoanStatementRequestDto;
import ru.neoflex.calculator_microservice.dto.ScoringDataDto;
import ru.neoflex.calculator_microservice.services.CalculateService;
import ru.neoflex.calculator_microservice.services.LoanOffersService;
import ru.neoflex.calculator_microservice.util.exceptions.GenderException;
import ru.neoflex.calculator_microservice.util.exceptions.LoanIsNotApprovedException;

import java.util.List;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController("/calculator")
public class CalculatorController {

    private final CalculateService calculateService;
    private final LoanOffersService loanOffersService;

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDto>> offers(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto) {
        log.info("Loan statement request has been received");
        return ResponseEntity.ok(loanOffersService.getLoanOffersDto(loanStatementRequestDto));
    }

    @PostMapping("/calc")
    public CreditDto offers(@RequestBody @Valid ScoringDataDto scoringDataDto) throws LoanIsNotApprovedException, GenderException {
        log.info("The data for the scoring has been received");
        return calculateService.getCreditDto(scoringDataDto);
    }
}
