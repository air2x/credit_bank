package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Passport;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@Transactional
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatementService {

    public static final String BASE_MS_CALC_URL = "http://localhost:8080/calculator";
    public static final String OFFERS_MS_CALC_URL = "/offers";

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    public List<LoanOfferDto> getLoanOffersDto(LoanStatementRequestDto loanStatementRequestDto) {
        Client client;
        Statement statement;
        List<LoanOfferDto> offers;
        if (loanStatementRequestDto != null) {
            createClient(loanStatementRequestDto);
            RestClient restClient = RestClient.create();
            offers = restClient.post()
                    .uri(BASE_MS_CALC_URL + OFFERS_MS_CALC_URL)
                    .contentType(APPLICATION_JSON)
                    .body(loanStatementRequestDto)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            client = createClient(loanStatementRequestDto);
            statement = new Statement();
            statement.setStatementId(UUID.randomUUID());
            statement.setClientId(client.getClientId());
            for (LoanOfferDto l : offers) {
                l.setStatementId(statement.getStatementId());
            }
        } else {
            throw new MSDealException("LoanStatementRequestDto is not be null");
        }
        saveClient(client);
        saveStatement(statement);
        return offers;
    }

    @Transactional
    public void saveClient(Client client) {
        if (client != null) {
            clientRepository.save(client);
        } else {
            throw new MSDealException("Client is not created");
        }
    }

    @Transactional
    public void saveStatement(Statement statement) {
        if (statement != null) {
            statementRepository.save(statement);
        } else {
            throw new MSDealException("Statement is not created");
        }
    }

    private Client createClient(LoanStatementRequestDto loanStatementRequestDto) {
        Client client = new Client();
        Passport passport = new Passport();
        Statement statement = new Statement();
        client.setClientId(UUID.randomUUID());
        client.setLastName(loanStatementRequestDto.getLastName());
        client.setFirstName(loanStatementRequestDto.getFirstName());
        client.setMiddleName(loanStatementRequestDto.getMiddleName());
        client.setBirthDate(loanStatementRequestDto.getBirthday());
        client.setEmail(loanStatementRequestDto.getEmail());

        passport.setPassport_uuid(UUID.randomUUID());
        passport.setSeries(loanStatementRequestDto.getPassportSeries());
        passport.setNumber(loanStatementRequestDto.getPassportNumber());

        statement.setStatementId(UUID.randomUUID());
        statement.setClientId(client.getClientId());

        client.setPassportId(passport);
        return client;
    }
}
