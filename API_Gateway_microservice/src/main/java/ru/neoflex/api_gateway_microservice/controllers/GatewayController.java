package ru.neoflex.api_gateway_microservice.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.api_gateway_microservice.services.GatewayService;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;

@Slf4j
@RestController
@AllArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping("/deal/statement")
    public ResponseEntity<?> getLoanOffersDto(@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return gatewayService.getLoanOffers(loanStatementRequestDto);
    }

    @PostMapping("/deal/offer/select")
    public void choosingOffer(@RequestBody LoanOfferDto loanOfferDto) {
        gatewayService.choosingOffer(loanOfferDto);
    }

    @PostMapping("/deal/calculate/{statementId}")
    public void finishCalculate(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                @PathVariable String statementId) {
        gatewayService.finishCalculate(finishRegistrationRequestDto, statementId);
    }

    @PostMapping("/deal/document/{statementId}/send")
    public void sendDoc(@PathVariable String statementId) {
        gatewayService.sendDoc(statementId);
    }

    @PostMapping("/deal/document/{statementId}/sign")
    public void singDoc(@PathVariable String statementId) {
        gatewayService.singDoc(statementId);
    }

    @PostMapping("/deal/document/{statementId}/code")
    public void sendDocFinish(@PathVariable String statementId,
                              @RequestBody String code) {
        gatewayService.sendDocFinish(statementId, code);
    }
}
