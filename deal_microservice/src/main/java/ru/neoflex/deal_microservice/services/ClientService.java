package ru.neoflex.deal_microservice.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Employment;
import ru.neoflex.deal_microservice.model.Passport;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanStatementRequestDto;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper mapper;

    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    public Client createClient(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            throw new MSDealException("LoanStatementRequestDto cannot be null");
        }
        Client client = new Client();
        client.setFirstName(loanStatementRequestDto.getFirstName());
        client.setLastName(loanStatementRequestDto.getLastName());
        client.setMiddleName(loanStatementRequestDto.getMiddleName());
        client.setEmail(loanStatementRequestDto.getEmail());
        client.setBirthDate(loanStatementRequestDto.getBirthday());
        client.setPassportId(createPassport(loanStatementRequestDto));
        log.info("Client " + client.getId() + " successfully created");
        return client;
    }

    public Client getClient(UUID clientId) {
        return clientRepository.getReferenceById(clientId);
    }

    public Client addInfoFromFinishRegistrationRequestDto(FinishRegistrationRequestDto finishRegistrationRequestDto, Client client) {
        if (finishRegistrationRequestDto == null) {
            throw new MSDealException("FinishRegistrationRequestDto cannot be null");
        }
        if (client == null) {
            throw new MSDealException("Client cannot be null");
        }
        client.setGender(finishRegistrationRequestDto.getGender());
        client.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());

        Passport passport = client.getPassportId();
        passport.setIssueBranch(finishRegistrationRequestDto.getPassportIssueBrach());
        passport.setIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
        client.setPassportId(passport);
        Employment employment = mapper.map(finishRegistrationRequestDto.getEmployment(), Employment.class);
        employment.setEmploymentId(UUID.randomUUID());
        client.setEmploymentId(employment);
        client.setAccountNumber(finishRegistrationRequestDto.getAccountNumber());

        saveClient(client);
        return client;
    }

    private Passport createPassport(LoanStatementRequestDto loanStatementRequestDto) {
        if (loanStatementRequestDto == null) {
            throw new MSDealException("LoanStatementRequestDto cannot be null");
        }
        Passport passport = new Passport();
        passport.setSeries(loanStatementRequestDto.getPassportSeries());
        passport.setNumber(loanStatementRequestDto.getPassportNumber());
        passport.setPassport_uuid(UUID.randomUUID());
        return passport;
    }
}
