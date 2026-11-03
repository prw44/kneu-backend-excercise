package com.kneu.medications;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CollectionTable;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @ElementCollection
    @CollectionTable(name = "prescription_times", joinColumns = @JoinColumn(name = "prescription_id"))
    @Column(name = "time_of_day", nullable = false)
    private Set<LocalTime> timesOfDay = new HashSet<>();

    private String label;

    protected Prescription() {
    }

    public Prescription(Medication medication, Set<LocalTime> timesOfDay, String label) {
        this.medication = medication;
        this.timesOfDay = new HashSet<>(timesOfDay);
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public Medication getMedication() {
        return medication;
    }

    public Set<LocalTime> getTimesOfDay() {
        return Set.copyOf(timesOfDay);
    }

    public String getLabel() {
        return label;
    }
}
