package com.kneu.medications;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    @Override
    @EntityGraph(attributePaths = {"timesOfDay", "medication"})
    List<Prescription> findAll();
}

