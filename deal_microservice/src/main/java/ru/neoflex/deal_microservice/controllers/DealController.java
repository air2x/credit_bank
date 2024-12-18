package ru.neoflex.deal_microservice.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
//    private final SelectService selectService;
//    private final FinishRegistrationService finishRegistrationService;
    private final RequestInMSCalcService requestInMSCalcService;
/*
По API приходит LoanStatementRequestDto
На основе LoanStatementRequestDto создаётся сущность Client и сохраняется в БД.
Создаётся Statement со связью на только что созданный Client и сохраняется в БД.
Отправляется POST запрос на /calculator/offers МС Калькулятор через RestClient
Каждому элементу из списка List<LoanOfferDto> присваивается id созданной заявки (Statement)
Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему".
 */
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
    /*
    По API приходит LoanOfferDto
Достаётся из БД заявка(Statement) по statementId из LoanOfferDto.
В заявке обновляется статус, история статусов(List<StatementStatusHistoryDto>), принятое предложение LoanOfferDto
устанавливается в поле appliedOffer.
Заявка сохраняется.
     */

    @PostMapping("/offer/select")
    public void choosingOffer(@RequestBody LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            throw new MSDealException("Loan offer is not be null");
        } else {
            statementService.addLoanOfferInStatement(loanOfferDto);
            log.info("Loan statement request has been saved");
        }
    }

    /*
    По API приходит объект FinishRegistrationRequestDto и параметр statementId (String).
Достаётся из БД заявка(Statement) по statementId.
ScoringDataDto насыщается информацией из FinishRegistrationRequestDto и Client, который хранится в Statement
Отправляется POST запрос на /calculator/calc МС Калькулятор с телом ScoringDataDto через RestClient.
На основе полученного из кредитного конвейера CreditDto создаётся сущность Credit и сохраняется в базу со статусом CALCULATED.
В заявке обновляется статус, история статусов.
Заявка сохраняется.
     */

    @PostMapping("/deal/calculate/{statementId}")
    public void finishCalculate(@RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                                @PathVariable String statementId) {
        if (finishRegistrationRequestDto == null) {
            throw new MSDealException("Finish registration request is not be null");
        } else {
            requestInMSCalcService.calculateFinish(finishRegistrationRequestDto, statementId);
            log.info("Finish registration request has been saved");
        }
    }
}
