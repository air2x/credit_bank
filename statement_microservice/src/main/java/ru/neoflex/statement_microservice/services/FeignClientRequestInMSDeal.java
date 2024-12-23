package ru.neoflex.statement_microservice.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.neoflex.dto.CreditDto;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.dto.ScoringDataDto;

import java.util.List;

@FeignClient(name = "Request-in-MSDeal", url = "http://localhost:8081/deal")
public interface FeignClientRequestInMSDeal {

    @PostMapping("/statement")
    List<LoanOfferDto> offers(@RequestBody LoanStatementRequestDto loanStatementRequestDto);

    @PostMapping("/offer/select")
    CreditDto offers(@RequestBody LoanOfferDto loanOfferDto);
}
