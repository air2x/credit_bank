package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.*;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.deal_microservice.repositories.CreditRepository;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.CreditDto;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.ScoringDataDto;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static ru.neoflex.deal_microservice.services.StatementService.BASE_MS_CALC_URL;
import static ru.neoflex.deal_microservice.services.StatementService.addStatusHistory;
import static ru.neoflex.enums.ApplicationStatus.APPROVED;
import static ru.neoflex.enums.CreditStatus.CALCULATED;

@Service
@Transactional
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FinishRegistrationService {

    public static final String CALC_MS_CALC_URL = "/calc";

    private final StatementRepository statementRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ModelMapper mapper;

    @Transactional
    public void finallyRegisters(FinishRegistrationRequestDto finishRegistrationRequestDto, String statementId) {
        ScoringDataDto scoringDataDto = createScoringDataDto(finishRegistrationRequestDto, statementId);
        RestClient restClient = RestClient.create();
        CreditDto creditDto = restClient.post()
                .uri(BASE_MS_CALC_URL + CALC_MS_CALC_URL)
                .contentType(APPLICATION_JSON)
                .body(scoringDataDto)
                .retrieve()
                .body(CreditDto.class);

        Statement statement = statementRepository
                .findById(UUID.fromString(statementId))
                .orElseThrow(() -> new MSDealException("Statement with id " + statementId + " was not be find"));

        Credit credit = mapper.map(creditDto, Credit.class);
        credit.setCreditId(UUID.randomUUID());
        credit.setCreditStatus(CALCULATED);

        creditRepository.save(credit);

        statement.setCreditId(credit.getCreditId());
        addStatusHistory(statement, APPROVED);
        statementRepository.save(statement);
    }

    @Transactional
    public ScoringDataDto createScoringDataDto(FinishRegistrationRequestDto finishRegistrationRequestDto, String statementId) {
        ScoringDataDto scoringDataDto;

        Statement statement = statementRepository
                .findById(UUID.fromString(statementId))
                .orElseThrow(() -> new MSDealException("Statement with id " + statementId + " was not be find"));

        Client client = clientRepository
                .findById(statement.getClientId())
                .orElseThrow(() -> new MSDealException("Client with id " + statement.getClientId() + " was not be find"));

        scoringDataDto = fillsScoringDataDto(fillsClient(client, finishRegistrationRequestDto));
        return scoringDataDto;
    }

    private Client fillsClient(Client client, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        if (client != null && finishRegistrationRequestDto != null) {
            client.setGender(finishRegistrationRequestDto.getGender());
            client.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
            client.setEmploymentId(mapper.map(finishRegistrationRequestDto.getEmployment(), Employment.class));

            Passport passport = client.getPassportId();
            passport.setIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
            passport.setIssueBranch(finishRegistrationRequestDto.getPassportIssueBrach());
        } else if (client == null) {
            log.info("Client is null in fillsClient");
            throw new MSDealException("Client is not be null");
        } else {
            log.info("Finish registration request is null in fillsClient");
            throw new MSDealException("Finish registration request is not be null");
        }
        log.info("Filling in the client successfully");
        return client;
    }

    private ScoringDataDto fillsScoringDataDto(Client client) {
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        if (client != null) {
            scoringDataDto.setFirstName(client.getFirstName());
            scoringDataDto.setLastName(client.getLastName());
            scoringDataDto.setMiddleName(client.getMiddleName());
            scoringDataDto.setBirthday(client.getBirthDate());
            scoringDataDto.setGender(client.getGender());
            scoringDataDto.setMaritalStatus(client.getMaritalStatus());
            scoringDataDto.setDependentAmount(client.getDependentAmount());
        } else {
            log.info("Client is null in fillsScoringDataDto");
            throw new MSDealException("Client is not be null");
        }
        log.info("Filling in the scoringDataDto successfully");
        return scoringDataDto;
    }
}
