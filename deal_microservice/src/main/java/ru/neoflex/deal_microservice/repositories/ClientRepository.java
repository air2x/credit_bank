package ru.neoflex.deal_microservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.deal_microservice.model.Client;


import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
