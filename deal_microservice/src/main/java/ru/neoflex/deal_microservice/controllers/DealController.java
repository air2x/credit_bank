package ru.neoflex.deal_microservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.services.RequestInMSCalcService;
import ru.neoflex.deal_microservice.services.StatementService;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;

@Slf4j
@RestController
@RequestMapping("/deal")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DealController {

    private final StatementService statementService;
    private final RequestInMSCalcService requestInMSCalcService;

    @PostMapping("/statement")
    public ResponseEntity<?> getLoanOffersDto(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        log.info("Loan statement request has been received");
        return ResponseEntity.ok(requestInMSCalcService.getLoanOffers(loanStatementRequestDto));
    }

    @PostMapping("/offer/select")
    public void choosingOffer(@RequestBody LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            throw new MSDealException("Loan offer is not be null");
        } else {
            statementService.addLoanOfferInStatement(loanOfferDto);
            log.info("Loan statement request has been saved");
        }
    }

    @PostMapping("/deal/calculate/{statementId}")
    public void finishCalculate(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                @PathVariable String statementId) {
        if (finishRegistrationRequestDto == null) {
            throw new MSDealException("Finish registration request with id " + statementId + " is not be null");
        } else {
            requestInMSCalcService.calculateFinish(finishRegistrationRequestDto, statementId);
            log.info("Finish registration request with id " + statementId + " has been saved");
        }
    }

    @ExceptionHandler
    private ResponseEntity<MSDealException> handleException(MSDealException ex) {
        MSDealException response = new MSDealException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
