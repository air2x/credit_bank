package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;


import static ru.neoflex.deal_microservice.services.StatementService.addStatusHistory;
import static ru.neoflex.enums.ApplicationStatus.DOCUMENT_CREATED;

@Service
@Transactional
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SelectService {

    private final StatementRepository statementRepository;

    @Transactional
    public void saveStatement(LoanOfferDto loanOfferDto) {
        if (loanOfferDto != null) {
            Statement statement = statementRepository
                    .findById(loanOfferDto.getStatementId())
                    .orElseThrow(() -> new MSDealException("Statement with id " + loanOfferDto.getStatementId() + " was not" +
                            "be find"));
            addStatusHistory(statement, DOCUMENT_CREATED);
            statement.setAppliedOffer(loanOfferDto);
        }
    }
}
