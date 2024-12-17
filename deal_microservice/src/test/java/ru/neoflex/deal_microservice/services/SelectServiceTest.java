package ru.neoflex.deal_microservice.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.neoflex.deal_microservice.exceptions.MSDealException;
import ru.neoflex.deal_microservice.model.Statement;
import ru.neoflex.deal_microservice.repositories.StatementRepository;
import ru.neoflex.dto.LoanOfferDto;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SelectServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private SelectService selectService;

    LoanOfferDto loanOfferDto;
    Statement statement;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(UUID.randomUUID());

        statement = new Statement();
        statement.setStatementId(loanOfferDto.getStatementId());
    }

    @Test
    void testSaveStatementTest() {
        when(statementRepository.findById(loanOfferDto.getStatementId())).thenReturn(Optional.of(statement));
        selectService.saveStatement(loanOfferDto);
        assertEquals(loanOfferDto, statement.getAppliedOffer());
    }

    @Test
    void testLoanOfferDtoNotNullTest() {
        loanOfferDto = null;
        Exception ex = Assertions.assertThrows(MSDealException.class, () -> selectService.saveStatement(loanOfferDto));
        Assertions.assertEquals("LoanOfferDto is not be null", ex.getMessage());
    }
}