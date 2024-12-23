package ru.neoflex.statement_microservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;
import ru.neoflex.statement_microservice.exceptions.MSStatementException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestInMSDealService {

    private final FeignClientRequestInMSDeal feignClientRequestInMSDeal;

    public Object getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            throw new MSStatementException("LoanStatementRequestDto cannot be null");
        }
        return feignClientRequestInMSDeal.offers(loanStatementRequestDto);
    }

    public void addLoanOfferInStatement(LoanOfferDto loanOfferDto) {
        if (loanOfferDto == null) {
            throw new MSStatementException("LoanOfferDto cannot be null");
        }
        feignClientRequestInMSDeal.offers(loanOfferDto);
    }
}
