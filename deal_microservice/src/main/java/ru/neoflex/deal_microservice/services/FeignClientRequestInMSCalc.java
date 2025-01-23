package ru.neoflex.deal_microservice.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.neoflex.dto.CreditDto;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.dto.ScoringDataDto;

import java.util.List;

@FeignClient(name = "Request-in-MSCalculator", url = "http://localhost:8080/calculator")
public interface FeignClientRequestInMSCalc {

    @PostMapping("/offers")
    List<LoanOfferDto> offers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);

    @PostMapping("/calc")
    CreditDto offers(@RequestBody ScoringDataDto scoringDataDto);
}
