package ru.neoflex.api_gateway_microservice.controllers;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.api_gateway_microservice.exceptions.MSGatewayException;
import ru.neoflex.api_gateway_microservice.services.FeignClientRequestInDealMS;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;


@Slf4j
@RestController
@AllArgsConstructor
public class GatewayController {

    private final FeignClientRequestInDealMS myFeignClient;

    @PostMapping("/deal/statement")
    public ResponseEntity<?> getLoanOffersDto(@RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            log.error("LoanStatementRequestDto can not be null");
            throw new MSGatewayException("LoanStatementRequestDto can not be null");
        }
        try {
            log.info("choosingOffer completed successfully");
            return myFeignClient.getLoanOffersDto(loanStatementRequestDto);
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new MSGatewayException(e.getMessage());
        }
    }

    @PostMapping("/deal/offer/select")
    public void choosingOffer(@RequestBody LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            log.error("LoanOfferDto in choosingOffer null");
            throw new MSGatewayException("LoanOfferDto can not be null");
        }
        try {
            myFeignClient.choosingOffer(loanOfferDto);
            log.info("choosingOffer completed successfully");
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new MSGatewayException(e.getMessage());
        }
    }

    @PostMapping("/deal/calculate/{statementId}")
    public void finishCalculate(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                @PathVariable String statementId) {
        if (finishRegistrationRequestDto == null) {
            log.error("FinishRegistrationRequestDto in finishCalculate null");
            throw new MSGatewayException("FinishRegistrationRequestDto can not be null");
        }
        if (statementId == null) {
            log.error("statementId in finishCalculate null");
            throw new MSGatewayException("StatementId can not be null");
        }
        try {
            myFeignClient.finishCalculate(finishRegistrationRequestDto, statementId);
            log.info("finishCalculate completed successfully");
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new MSGatewayException(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/send")
    public void sendDoc(@PathVariable String statementId) {
        if (statementId == null) {
            log.error("statementId in sendDoc null");
            throw new MSGatewayException("StatementId can not be null");
        }
        try {
            myFeignClient.sendDoc(statementId);
            log.info("sendDoc completed successfully");
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new MSGatewayException(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/sign")
    public void singDoc(@PathVariable String statementId) {
        if (statementId == null) {
            log.error("statementId in singDoc null");
            throw new MSGatewayException("StatementId can not be null");
        }
        try {
            myFeignClient.singDoc(statementId);
            log.info("singDoc completed successfully");
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new MSGatewayException(e.getMessage());
        }
    }

    @PostMapping("/deal/document/{statementId}/code")
    public void sendDocFinish(@PathVariable String statementId,
                              @RequestBody String code) {
        if (statementId == null) {
            log.error("statementId in sendDocFinish null");
            throw new MSGatewayException("StatementId can not be null");
        }
        if (code == null) {
            log.error("code in sendDocFinish null");
            throw new MSGatewayException("Code can not be null");
        }
        try {
            myFeignClient.sendDocFinish(statementId, code);
            log.info("sendDocFinish completed successfully");
        } catch (FeignException e) {
            log.error(e.getMessage());
            throw new MSGatewayException(e.getMessage());
        }
    }
}
