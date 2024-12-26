package ru.neoflex.statement_microservice.services;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.statement_microservice.exceptions.MSStatementException;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestInMSDealService {

    private final FeignClientRequestInMSDeal feignClientRequestInMSDeal;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            log.info("LoanStatementRequestDto is null in getLoanOffers");
            throw new MSStatementException("LoanStatementRequestDto cannot be null");
        }
        log.info("LoanStatementRequestDto with email " + loanStatementRequestDto.getEmail() + " request has been received");
        try {
            return feignClientRequestInMSDeal.offers(loanStatementRequestDto);
        } catch (FeignException e) {
            throw new MSStatementException(e.getMessage());
        }
    }

    public void addLoanOfferInStatement(LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            throw new MSStatementException("LoanOfferDto cannot be null");
        }
        log.info("Loan statement with statement id " + loanOfferDto.getStatementId() + " request has been saved");
        try {
            feignClientRequestInMSDeal.offers(loanOfferDto);
        } catch (FeignException e) {
            throw new MSStatementException(e.getMessage());
        }
    }
}
