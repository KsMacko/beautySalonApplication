package com.example.demo.Repositories;

import com.example.demo.Entities.Client;
import com.example.demo.Entities.Master;
import com.example.demo.Entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<List<Schedule>> findAllByMasterAndNoteDate(Master master, LocalDate date);
    Optional<List<Schedule>> findAllByClient(Client client);

    Optional<List<Schedule>> findAllByNoteDate(LocalDate date);
}
