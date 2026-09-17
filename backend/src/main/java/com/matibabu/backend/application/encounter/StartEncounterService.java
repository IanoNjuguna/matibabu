package com.matibabu.backend.application.encounter;

import com.matibabu.backend.config.NodeIdentity;
import com.matibabu.backend.domain.department.Department;
import com.matibabu.backend.domain.department.DepartmentRepository;
import com.matibabu.backend.domain.encounter.Encounter;
import com.matibabu.backend.domain.encounter.EncounterRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class StartEncounterService implements StartEncounterUseCase {

    private final EncounterRepository encounterRepository;
    private final DepartmentRepository departmentRepository;
    private final NodeIdentity nodeIdentity;

    public StartEncounterService(
            EncounterRepository encounterRepository,
            DepartmentRepository departmentRepository,
            NodeIdentity nodeIdentity
    ) {
        this.encounterRepository = encounterRepository;
        this.departmentRepository = departmentRepository;
        this.nodeIdentity = nodeIdentity;
    }

    @Override
    public Encounter start(
            UUID patientId,
            UUID attendingClinicianId,
            UUID departmentId,
            Instant now
    ) {
        UUID facilityId = nodeIdentity.facilityId();

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Department not found: " + departmentId
                ));

        if (!department.getFacilityId().equals(facilityId)) {
            throw new IllegalArgumentException(
                    "Department does not belong to the current facility"
            );
        }

        Encounter encounter = Encounter.start(
                patientId,
                attendingClinicianId,
                facilityId,
                departmentId,
                now
        );

        return encounterRepository.save(encounter);
    }
}

