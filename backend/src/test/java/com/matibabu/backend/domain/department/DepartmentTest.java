package com.matibabu.backend.domain.department;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentTest {

    private final UUID facilityId = UUID.randomUUID();

    @Test
    void shouldCreateDepartment() {
        Department department =
                Department.create(
                        facilityId,
                        "OPD",
                        "Outpatient Department"
                );

        assertNotNull(department.getId());
        assertEquals(facilityId, department.getFacilityId());
        assertEquals("OPD", department.getCode());
        assertEquals(
                "Outpatient Department",
                department.getName()
        );
        assertTrue(department.isActive());
    }

    @Test
    void shouldRejectNullFacilityId() {
        assertThrows(
                NullPointerException.class,
                () -> Department.create(
                        null,
                        "OPD",
                        "Outpatient Department"
                )
        );
    }

    @Test
    void shouldRejectBlankCode() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Department.create(
                        facilityId,
                        " ",
                        "Outpatient Department"
                )
        );
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Department.create(
                        facilityId,
                        "OPD",
                        " "
                )
        );
    }

    @Test
    void shouldDeactivateDepartment() {
        Department department =
                Department.create(
                        facilityId,
                        "OPD",
                        "Outpatient Department"
                );

        department.deactivate();

        assertFalse(department.isActive());
    }

    @Test
    void shouldReactivateDepartment() {
        Department department =
                Department.create(
                        facilityId,
                        "OPD",
                        "Outpatient Department"
                );

        department.deactivate();
        department.reactivate();

        assertTrue(department.isActive());
    }

    @Test
    void shouldRenameDepartment() {
        Department department =
                Department.create(
                        facilityId,
                        "OPD",
                        "Outpatient Department"
                );

        department.rename("Outpatient Clinic");

        assertEquals(
                "Outpatient Clinic",
                department.getName()
        );
    }

    @Test
    void shouldChangeCode() {
        Department department =
                Department.create(
                        facilityId,
                        "OPD",
                        "Outpatient Department"
                );

        department.changeCode("OUTPATIENT");

        assertEquals("OUTPATIENT", department.getCode());
    }
}