package com.matibabu.backend.api.encounter;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartEncounterRequest(

        @NotNull
        UUID patientId,

        @NotNull
        UUID departmentId

) {
}
