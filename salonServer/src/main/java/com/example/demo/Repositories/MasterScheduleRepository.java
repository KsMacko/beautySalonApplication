package com.example.demo.Repositories;

import com.example.demo.Entities.MasterSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterScheduleRepository extends JpaRepository<MasterSchedule,Long> {
}
