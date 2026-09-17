
        package com.matibabu.backend.infrastructure.persistence.encounter;

import com.matibabu.backend.domain.encounter.EncounterStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "encounters")
public class EncounterEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID patientId;

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID attendingClinicianId;

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID facilityId;

    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID departmentId;

    private Instant startedAt;

    @Enumerated(EnumType.STRING)
    private EncounterStatus status;

    private Instant endedAt;

    protected EncounterEntity() {
        // Required by JPA.
    }

    // Setters

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public void setAttendingClinicianId(UUID attendingClinicianId) {
        this.attendingClinicianId = attendingClinicianId;
    }

    public void setFacilityId(UUID facilityId) {
        this.facilityId = facilityId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public void setStatus(EncounterStatus status) {
        this.status = status;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getAttendingClinicianId() {
        return attendingClinicianId;
    }

    public UUID getFacilityId() {
        return facilityId;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }
}

