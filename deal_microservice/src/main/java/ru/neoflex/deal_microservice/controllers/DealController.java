package ru.neoflex.deal_microservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.services.FinishRegistrationService;
import ru.neoflex.deal_microservice.services.SelectService;
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
    private final SelectService selectService;
    private final FinishRegistrationService finishRegistrationService;

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
        return ResponseEntity.ok(statementService.getLoanOffersDto(loanStatementRequestDto));
    }

    @PostMapping("/offer/select")
    public void choosingOffer(@RequestBody LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            throw new MSDealException("Loan offer is not be null");
        } else {
            selectService.saveStatement(loanOfferDto);
            log.info("Loan statement request has been saved");
        }
    }

    @PostMapping("/deal/calculate/{statementId}")
    public void finishCalculate(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                @PathVariable String statementId) {
        if (finishRegistrationRequestDto == null) {
            throw new MSDealException("Finish registration request is not be null");
        } else {
            finishRegistrationService.finallyRegisters(finishRegistrationRequestDto, statementId);
            log.info("Finish registration request has been saved");
        }
    }
}
