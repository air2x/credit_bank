package ru.neoflex.deal_microservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.dto.LoanOfferDto;
import ru.neoflex.dto.LoanStatementRequestDto;


import java.util.List;

@Slf4j
@Service
public class StatementService {

    private final ClientRepository clientRepository;

    @Autowired
    public StatementService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<LoanOfferDto> getLoanOffersDto(LoanStatementRequestDto loanStatementRequestDto) {
        return null;
    }

    private Client createClient(LoanStatementRequestDto loanStatementRequestDto) {
        Client client = new Client();
        saveClient(client);
        return client;
    }

    private void saveClient(Client client) {
        if (client != null) {
            clientRepository.save(client);
        } else {
            throw new MSDealException("Client is not created");
        }
    }
}
