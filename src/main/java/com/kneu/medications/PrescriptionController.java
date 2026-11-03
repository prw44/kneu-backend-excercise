package com.kneu.medications;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionRepository repository;
    private final MedicationRepository medicationRepository;

    public PrescriptionController(PrescriptionRepository repository,
                                  MedicationRepository medicationRepository) {
        this.repository = repository;
        this.medicationRepository = medicationRepository;
    }

    @GetMapping
    public List<Prescription> getAllPrescriptions() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody CreatePrescriptionRequest request) {
        if (request.medicationId() == null || request.timesOfDay() == null || request.timesOfDay().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "medicationId and at least one timeOfDay are required"
            );
        }

        Medication medication = medicationRepository.findById(request.medicationId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Medication not found: " + request.medicationId()
            ));

        Prescription prescription = new Prescription(
            medication,
            request.timesOfDay(),
            request.label()
        );

        Prescription saved = repository.save(prescription);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    public record CreatePrescriptionRequest(
        Long medicationId,
        Set<LocalTime> timesOfDay,
        String label
    ) {
    }
}
