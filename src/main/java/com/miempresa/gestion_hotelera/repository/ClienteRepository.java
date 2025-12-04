package com.miempresa.gestion_hotelera.repository;

import com.miempresa.gestion_hotelera.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
