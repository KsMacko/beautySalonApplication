package com.example.demo.Repositories;

import com.example.demo.Entities.Master;
import com.example.demo.Entities.SalonService;
import com.example.demo.Entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MasterRepository extends JpaRepository<Master, String> {
    Optional<Master> findByMasterLogin(Long login);
    Optional<List<Master>> findAllByService(SalonService service);
}
