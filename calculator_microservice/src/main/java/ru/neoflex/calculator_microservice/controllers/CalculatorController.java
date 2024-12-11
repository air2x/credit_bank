package ru.neoflex.calculator_microservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.calculator_microservice.services.CalculateService;
import ru.neoflex.calculator_microservice.services.LoanOffersService;
import ru.neoflex.calculator_microservice.util.exceptions.*;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.dto.ScoringDataDto;


@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RestController("/calculator")
public class CalculatorController {

    private final CalculateService calculateService;
    private final LoanOffersService loanOffersService;

    @PostMapping("/offers")
    public ResponseEntity<?> offers(@RequestBody @Valid LoanStatementRequestDto loanStatementRequestDto,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        log.info("Loan statement request has been received");
        return ResponseEntity.ok(loanOffersService.getLoanOffersDto(loanStatementRequestDto));
    }

    @PostMapping("/calc")
    public ResponseEntity<?> offers(@RequestBody @Valid ScoringDataDto scoringDataDto,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessage.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        log.info("The data for the scoring has been received");
        return ResponseEntity.ok(calculateService.getCreditDto(scoringDataDto));
    }

    @ExceptionHandler
    private ResponseEntity<GenderException> handleException(GenderException ex) {
        GenderException response = new GenderException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<LoanIsNotApprovedException> handleException(LoanIsNotApprovedException ex) {
        LoanIsNotApprovedException response = new LoanIsNotApprovedException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullAmountException> handleException(NullAmountException ex) {
        NullAmountException response = new NullAmountException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullBirthDayException> handleException(NullBirthDayException ex) {
        NullBirthDayException response = new NullBirthDayException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullEmailException> handleException(NullEmailException ex) {
        NullEmailException response = new NullEmailException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullEmploymentException> handleException(NullEmploymentException ex) {
        NullEmploymentException response = new NullEmploymentException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullMonthlyPaymentException> handleException(NullMonthlyPaymentException ex) {
        NullMonthlyPaymentException response = new NullMonthlyPaymentException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullRateException> handleException(NullRateException ex) {
        NullRateException response = new NullRateException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    private ResponseEntity<NullTermException> handleException(NullTermException ex) {
        NullTermException response = new NullTermException(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
