package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Credit;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.CreditRepository;
import ru.neoflex.dto.CreditDto;

import static ru.neoflex.enums.ApplicationStatus.APPROVED;
import static ru.neoflex.enums.ChangeType.AUTOMATIC;
import static ru.neoflex.enums.CreditStatus.CALCULATED;

@Service
@Slf4j
@AllArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final StatementService statementService;
    private final ModelMapper mapper;

    @Transactional
    public void createAndSaveCreditAndSaveStatement(CreditDto creditDto, Statement statement) {
        if (creditDto == null) {
            throw new MSDealException("CreditDto cannot be null");
        }
        Credit credit = mapper.map(creditDto, Credit.class);
        credit.setCreditStatus(CALCULATED);
        creditRepository.save(credit);
        log.info("Credit " + credit.getId() + " successfully saved");

        statement.setCreditId(credit.getId());
        statementService.addStatementStatusHistory(statement, APPROVED, AUTOMATIC);
        statementService.saveStatement(statement);
    }
}
