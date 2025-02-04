package ru.neoflex.deal_microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.deal_microservice.model.Statement;

import java.util.UUID;

public interface StatementRepository extends JpaRepository<Statement, UUID> {

}
