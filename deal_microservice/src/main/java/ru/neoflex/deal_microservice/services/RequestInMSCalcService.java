package ru.neoflex.deal_microservice.services;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Employment;
import ru.neoflex.deal_microservice.model.Passport;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.dto.*;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestInMSCalcService {

    private final StatementService statementService;
    private final ClientService clientService;
    private final CreditService creditService;
    private final FeignClientRequestInMSCalc myFeignClient;
    private final ModelMapper mapper;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            throw new MSDealException("LoanStatementRequestDto cannot be null");
        }
        return getLoanOffersWithStatementId(loanStatementRequestDto);
    }

    public void calculateFinish(FinishRegistrationRequestDto finishRegistrationRequestDto, String statementId) {
        if (finishRegistrationRequestDto == null) {
            throw new MSDealException("FinishRegistrationRequestDto cannot be null");
        }
        if (statementId == null) {
            throw new MSDealException("StatementId cannot be null");
        }
        Statement statement = statementService.getStatement(UUID.fromString(statementId));
        ScoringDataDto scoringDataDto = createScoringDataDto(finishRegistrationRequestDto, statement);
        CreditDto creditDto = myFeignClient.offers(scoringDataDto);
        creditService.createAndSaveCreditAndSaveStatement(creditDto, statement);
    }

    private List<LoanOfferDto> getLoanOffersWithStatementId(LoanStatementRequestDto loanStatementRequestDto) {
        List<LoanOfferDto> offers = myFeignClient.offers(loanStatementRequestDto);
        Statement statement = createClientAndStatementAndGetStatement(loanStatementRequestDto);
        for (LoanOfferDto offer : offers) {
            offer.setStatementId(statement.getId());
        }
        return offers;
    }

    private Statement createClientAndStatementAndGetStatement(LoanStatementRequestDto loanStatementRequestDto) {
        Client client = clientService.createClient(loanStatementRequestDto);
        clientService.saveClient(client);
        Statement statement = statementService.createStatement(client);
        statementService.saveStatement(statement);
        return statement;
    }

    private ScoringDataDto createScoringDataDto(FinishRegistrationRequestDto finishRegistrationRequestDto, Statement statement) {
        ScoringDataDto scoringDataDto;
        Client client = clientService.getClient(statement.getClientId());
        client = clientService.addInfoFromFinishRegistrationRequestDto(finishRegistrationRequestDto, client);
        scoringDataDto = fillScoringDataDto(statement, client);
        return scoringDataDto;
    }

    private ScoringDataDto fillScoringDataDto(Statement statement, Client client) {
        if (statement == null) {
            throw new MSDealException("Statement cannot be null");
        }
        if (client == null) {
            throw new MSDealException("Client cannot be null");
        }
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        addClientInScoringDataDto(scoringDataDto, client);
        addStatementInScoringDataDto(scoringDataDto, statement);
        return scoringDataDto;
    }

    private void addStatementInScoringDataDto(ScoringDataDto scoringDataDto, Statement statement) {
        LoanOfferDto loanOfferDto = statement.getAppliedOffer();
        scoringDataDto.setAmount(loanOfferDto.getRequestAmount());
        scoringDataDto.setTerm(loanOfferDto.getTerm());
        scoringDataDto.setIsSalaryClient(loanOfferDto.getIsSalaryClient());
        scoringDataDto.setIsInsuranceEnabled(loanOfferDto.getIsInsuranceEnabled());
    }

    private void addClientInScoringDataDto(ScoringDataDto scoringDataDto, Client client) {
        scoringDataDto.setFirstName(client.getFirstName());
        scoringDataDto.setLastName(client.getLastName());
        scoringDataDto.setMiddleName(client.getMiddleName());
        scoringDataDto.setBirthday(client.getBirthDate());
        scoringDataDto.setGender(client.getGender());
        scoringDataDto.setAccountNumber(client.getAccountNumber());
        scoringDataDto.setMaritalStatus(client.getMaritalStatus());
        scoringDataDto.setDependentAmount(client.getDependentAmount());

        Passport passport = client.getPassportId();
        scoringDataDto.setPassportSeries(passport.getSeries());
        scoringDataDto.setPassportNumber(passport.getNumber());
        scoringDataDto.setPassportIssueBranch(passport.getIssueBranch());
        scoringDataDto.setPassportIssueDate(passport.getIssueDate());

        Employment employment = client.getEmploymentId();
        scoringDataDto.setEmployment(mapper.map(employment, EmploymentDto.class));
    }
}
