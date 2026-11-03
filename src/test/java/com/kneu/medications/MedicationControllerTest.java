package com.kneu.medications;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @BeforeEach
    void clearDatabase() {
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
    }

    @Test
    void getAllMedications() throws Exception {
        Medication aspirin = medicationRepository.save(new Medication("Aspirin", "100mg"));
        Medication metformin = medicationRepository.save(new Medication("Metformin", "500mg"));

        mockMvc.perform(get("/medications"))
            .andExpect(status().isOk())
            .andExpect(content().json("""
                [
                    {"id": %d, "name": "Aspirin", "dosage": "100mg"},
                    {"id": %d, "name": "Metformin", "dosage": "500mg"}
                ]
                """.formatted(aspirin.getId(), metformin.getId())));
    }
}
