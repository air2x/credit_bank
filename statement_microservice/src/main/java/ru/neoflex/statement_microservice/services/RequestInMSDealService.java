package ru.neoflex.statement_microservice.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestInMSDealService {

    public Object getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        return null;
    }

    public void addLoanOfferInStatement(LoanOfferDto loanOfferDto) {
    }
}
