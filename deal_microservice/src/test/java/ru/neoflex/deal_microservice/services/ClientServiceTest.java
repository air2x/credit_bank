package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Client;
import ru.neoflex.deal_microservice.model.Employment;
import ru.neoflex.deal_microservice.model.Passport;
import ru.neoflex.deal_microservice.repositories.ClientRepository;
import ru.neoflex.dto.EmploymentDto;
import ru.neoflex.dto.FinishRegistrationRequestDto;
import ru.neoflex.dto.LoanStatementRequestDto;

import java.time.LocalDate;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.neoflex.enums.EmploymentPosition.WORKER;
import static ru.neoflex.enums.Gender.MALE;
import static ru.neoflex.enums.MaritalStatus.SINGLE;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private ClientService clientService;

    private LoanStatementRequestDto loanStatementRequestDto;
    private FinishRegistrationRequestDto finishRegistrationRequestDto;
    private Client client;


    @BeforeEach
    public void setUp() {
        loanStatementRequestDto = new LoanStatementRequestDto();
        loanStatementRequestDto.setFirstName("Petr");
        loanStatementRequestDto.setLastName("Petrov");
        loanStatementRequestDto.setMiddleName("Petrovich");
        loanStatementRequestDto.setEmail("petr@mail.com");
        loanStatementRequestDto.setBirthday(LocalDate.ofEpochDay(1985 - 12 - 12));
        loanStatementRequestDto.setPassportSeries("1234");
        loanStatementRequestDto.setPassportNumber("123456");

        finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        finishRegistrationRequestDto.setGender(MALE);
        finishRegistrationRequestDto.setMaritalStatus(SINGLE);
        finishRegistrationRequestDto.setPassportIssueBrach("MVD");
        finishRegistrationRequestDto.setPassportIssueDate(LocalDate.ofEpochDay(2020 - 12 - 12));
        finishRegistrationRequestDto.setAccountNumber("1234567890");
        EmploymentDto employmentDto = new EmploymentDto();
        employmentDto.setEmployerINN("123456798");
        employmentDto.setPosition(WORKER);
        finishRegistrationRequestDto.setEmployment(employmentDto);

        client = new Client();
        Passport passport = new Passport();
        client.setPassportId(passport);
    }

    @Test
    void saveClient() {
        clientService.saveClient(client);
        verify(clientRepository).save(client);
    }

    @Test
    void createClientTest() {
        client = clientService.createClient(loanStatementRequestDto);

        Assertions.assertNotNull(client);
        Assertions.assertEquals(client.getFirstName(), loanStatementRequestDto.getFirstName());
        Assertions.assertEquals(client.getLastName(), loanStatementRequestDto.getLastName());
        Assertions.assertEquals(client.getMiddleName(), loanStatementRequestDto.getMiddleName());
        Assertions.assertEquals(client.getEmail(), loanStatementRequestDto.getEmail());
        Assertions.assertEquals(client.getBirthDate(), loanStatementRequestDto.getBirthday());
        Assertions.assertNotNull(client.getPassportId());
    }

    @Test
    void createClientTestIfLoanStatementRequestDtoIsNullTest() {
        Exception ex = Assertions.assertThrows(MSDealException.class, () -> clientService.createClient(null));
        Assertions.assertEquals("LoanStatementRequestDto cannot be null", ex.getMessage());
    }

    @Test
    void getClient() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        when(clientRepository.getReferenceById(clientId)).thenReturn(client);
        client = clientService.getClient(clientId);
        assertNotNull(client);
        verify(clientRepository).getReferenceById(clientId);
    }

    @Test
    void addInfoFromFinishRegistrationRequestDto() {
        Employment employment = new Employment();
        when(mapper.map(finishRegistrationRequestDto.getEmployment(), Employment.class)).thenReturn(employment);

        Client updatedClient = clientService.addInfoFromFinishRegistrationRequestDto(finishRegistrationRequestDto, client);

        Assertions.assertEquals(MALE, updatedClient.getGender());
        Assertions.assertEquals(SINGLE, updatedClient.getMaritalStatus());
        Passport passport = updatedClient.getPassportId();
        Assertions.assertEquals("MVD", passport.getIssueBranch());
        Assertions.assertEquals(LocalDate.ofEpochDay(2020 - 12 - 12), passport.getIssueDate());
        verify(clientRepository).save(updatedClient);
    }
}