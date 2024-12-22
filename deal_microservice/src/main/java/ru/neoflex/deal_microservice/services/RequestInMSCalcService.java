package ru.neoflex.deal_microservice.services;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Employment;
import ru.neoflex.deal_microservice.model.Passport;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.dto.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RequestInMSCalcService {

    public static final String BASE_MS_CALC_URL = "http://localhost:8080/calculator";
    public static final String OFFERS_MS_CALC_URL = "/offers";
    public static final String CALC_MS_CALC_URL = "/calc";

    private final StatementService statementService;
    private final ClientService clientService;
    private final CreditService creditService;
    private final ModelMapper mapper;

    public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            throw new MSDealException("LoanStatementRequestDto cannot be null");
        }
        List<LoanOfferDto> offers;
        RestClient restClient = RestClient.create();
        offers = restClient.post()
                .uri(BASE_MS_CALC_URL + OFFERS_MS_CALC_URL)
                .contentType(APPLICATION_JSON)
                .body(loanStatementRequestDto)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        UUID statementId = UUID.randomUUID();
        createClientAndStatement(loanStatementRequestDto, statementId);
        return getLoanOffersWithStatementId(offers, statementId);
    }

    public void calculateFinish(FinishRegistrationRequestDto finishRegistrationRequestDto, String statementId) {
        if (finishRegistrationRequestDto == null) {
            throw new MSDealException("FinishRegistrationRequestDto cannot be null");
        }
        if (statementId == null) {
            throw new MSDealException("StatementId cannot be null");
        }
        ScoringDataDto scoringDataDto = createScoringDataDto(finishRegistrationRequestDto, statementId);
        RestClient restClient = RestClient.create();
        CreditDto creditDto = restClient.post()
                .uri(BASE_MS_CALC_URL + CALC_MS_CALC_URL)
                .contentType(APPLICATION_JSON)
                .body(scoringDataDto)
                .retrieve()
                .body(CreditDto.class);
        creditService.createAndSaveCreditAndSaveStatement(creditDto, UUID.fromString(statementId));
    }

    private void createClientAndStatement(LoanStatementRequestDto loanStatementRequestDto, UUID statementId) {
        Client client = clientService.createClient(loanStatementRequestDto);
        clientService.saveClient(client);
        Statement statement = statementService.createStatement(client, statementId);
        statementService.saveStatement(statement);
    }

    private List<LoanOfferDto> getLoanOffersWithStatementId(List<LoanOfferDto> offers, UUID statementId) {
        for (LoanOfferDto offer : offers) {
            offer.setStatementId(statementId);
        }
        return offers;
    }

    private ScoringDataDto createScoringDataDto(FinishRegistrationRequestDto finishRegistrationRequestDto, String statementId) {
        ScoringDataDto scoringDataDto;
        Statement statement = statementService.getStatement(UUID.fromString(statementId));
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
