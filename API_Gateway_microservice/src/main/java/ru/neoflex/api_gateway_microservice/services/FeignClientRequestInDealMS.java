package ru.neoflex.api_gateway_microservice.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.neoflex.dto.*;

@FeignClient(name = "Request-in-MSDeal", url = "http://localhost:8081")
public interface FeignClientRequestInDealMS {

    @PostMapping("/deal/statement")
    ResponseEntity<?> getLoanOffersDto(@RequestBody LoanStatementRequestDto loanStatementRequestDto);

    @PostMapping("/deal/offer/select")
    void choosingOffer(@RequestBody LoanOfferDto loanOfferDto);

    @PostMapping("/deal/calculate/{statementId}")
    void finishCalculate(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                @PathVariable String statementId);

    @PostMapping("/deal/document/{statementId}/send")
    void sendDoc(@PathVariable String statementId);

    @PostMapping("/deal/document/{statementId}/sign")
    void singDoc(@PathVariable String statementId);

    @PostMapping("/deal/document/{statementId}/code")
    void sendDocFinish(@PathVariable String statementId, @RequestBody String code);
}
