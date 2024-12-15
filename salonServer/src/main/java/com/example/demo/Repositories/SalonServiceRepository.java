package com.example.demo.Repositories;

import com.example.demo.Entities.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
    Optional<SalonService> findByServiceId(Long id);
    Optional<SalonService> findByName(String name);
}
