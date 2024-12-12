package ru.neoflex.deal_microservice.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

/*
POST: /deal/statement

По API приходит LoanStatementRequestDto
На основе LoanStatementRequestDto создаётся сущность Client и сохраняется в БД.
Создаётся Statement со связью на только что созданный Client и сохраняется в БД.
Отправляется POST запрос на /calculator/offers МС Калькулятор через RestClient
Каждому элементу из списка List<LoanOfferDto> присваивается id созданной заявки (Statement)
Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему".

POST: /deal/offer/select

По API приходит LoanOfferDto
Достаётся из БД заявка(Statement) по statementId из LoanOfferDto.
В заявке обновляется статус, история статусов(List<StatementStatusHistoryDto>), принятое предложение LoanOfferDto
устанавливается в поле appliedOffer.
Заявка сохраняется.

POST: /deal/calculate/{statementId}

По API приходит объект FinishRegistrationRequestDto и параметр statementId (String).
Достаётся из БД заявка(Statement) по statementId.
ScoringDataDto насыщается информацией из FinishRegistrationRequestDto и Client, который хранится в Statement
Отправляется POST запрос на /calculator/calc МС Калькулятор с телом ScoringDataDto через RestClient.
На основе полученного из кредитного конвейера CreditDto создаётся сущность Credit и сохраняется в базу со статусом CALCULATED.
В заявке обновляется статус, история статусов.
Заявка сохраняется.
 */

@Service
@Transactional
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatementService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;

    public List<LoanOfferDto> getLoanOffersDto(LoanStatementRequestDto loanStatementRequestDto) {
        String url = "http://calculator_microservice/calculator/offers";
        createClient(loanStatementRequestDto);
        return null;
    }

    public void createClient(LoanStatementRequestDto loanStatementRequestDto) {
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

        client.setPassportId(passport.getPassport_uuid());

        saveStatement(statement);
        saveClient(client);
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
}
