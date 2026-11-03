package com.kneu.medications;

import java.time.LocalTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrescriptionControllerTest {

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
    void createsPrescriptionForExistingMedication() throws Exception {
        Medication medication = medicationRepository.save(new Medication("Aspirin", "100mg"));

        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "medicationId": %d,
                      "timesOfDay": ["08:00", "20:30"],
                      "label": "Daily"
                    }
                    """.formatted(medication.getId())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.medication.id").value(medication.getId()))
            .andExpect(jsonPath("$.medication.name").value("Aspirin"))
            .andExpect(jsonPath("$.timesOfDay").isArray())
            .andExpect(jsonPath("$.timesOfDay.length()").value(2))
            .andExpect(jsonPath("$.label").value("Daily"));
    }

    @Test
    void returnsNotFoundForUnknownMedication() throws Exception {
        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "medicationId": 999999,
                      "timesOfDay": ["08:00"]
                    }
                    """))
            .andExpect(status().isNotFound());
    }

    @Test
    void returnsBadRequestWhenTimesAreMissing() throws Exception {
        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"medicationId": 1}
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getsSavedPrescriptionsWithMedicationAndTimes() throws Exception {
        Medication medication = medicationRepository.save(new Medication("Aspirin", "100mg"));
        prescriptionRepository.save(new Prescription(
            medication,
            Set.of(LocalTime.of(8, 0)),
            "Morning dose"
        ));

        mockMvc.perform(get("/prescriptions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].medication.id").value(medication.getId()))
            .andExpect(jsonPath("$[0].medication.name").value("Aspirin"))
            .andExpect(jsonPath("$[0].timesOfDay[0]").value("08:00:00"))
            .andExpect(jsonPath("$[0].label").value("Morning dose"));
    }
}

