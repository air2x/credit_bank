package ru.neoflex.statement_microservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.statement_microservice.exceptions.MSStatementException;
import ru.neoflex.statement_microservice.services.RequestInMSDealService;

@Slf4j
@RestController
@RequestMapping(" /statement")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatementController {

    private final RequestInMSDealService requestInMSDealService;

    @PostMapping()
    public ResponseEntity<?> getLoanOffersDto(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        log.info("Loan statement request has been received");
        return ResponseEntity.ok(requestInMSDealService.getLoanOffers(loanStatementRequestDto));
    }

    @PostMapping("/offer")
    public void choosingOffer(@RequestBody LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            throw new MSStatementException("Loan offer is not be null");
        } else {
            requestInMSDealService.addLoanOfferInStatement(loanOfferDto);
            log.info("Loan statement with statement id " + loanOfferDto.getStatementId() + " request has been saved");
        }
    }

    @ExceptionHandler
    private ResponseEntity<MSStatementException> handleException(MSStatementException ex) {
        MSStatementException response = new MSStatementException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
