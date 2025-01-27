package ru.neoflex.deal_microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.deal_microservice.model.Credit;

import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
}
