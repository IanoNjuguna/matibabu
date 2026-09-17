package com.matibabu.backend.domain.department;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.Objects;
import java.util.UUID;


public class Department {

    private final UUID id;
    private final UUID facilityId;

    private String code;
    private String name;
    private boolean active;

    private Department(
            UUID id,
            UUID facilityId,
            String code,
            String name
    ) {
        this.id = Objects.requireNonNull(
                id,
                "Department ID cannot be null"
        );

        this.facilityId = Objects.requireNonNull(
                facilityId,
                "A department must belong to a facility"
        );

        this.code = requireText(
                code,
                "A department must have a code"
        );

        this.name = requireText(
                name,
                "A department must have a name"
        );

        this.active = true;
    }


    public static Department create(
            UUID facilityId,
            String code,
            String name
    ) {
        return new Department(
                UuidCreator.getTimeOrderedEpoch(),
                facilityId,
                code,
                name
        );
    }


    public static Department reconstitute(
            UUID id,
            UUID facilityId,
            String code,
            String name,
            boolean active
    ) {
        Department department = new Department(
                id,
                facilityId,
                code,
                name
        );

        department.active = active;

        return department;
    }

    public void rename(String name) {
        this.name = requireText(
                name,
                "A department must have a name"
        );
    }

    public void changeCode(String code) {
        this.code = requireText(
                code,
                "A department must have a code"
        );
    }

    public void deactivate() {
        this.active = false;
    }

    public void reactivate() {
        this.active = true;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFacilityId() {
        return facilityId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }
}