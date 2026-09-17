package com.matibabu.backend.infrastructure.persistence.department;

import com.matibabu.backend.domain.department.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentEntity toEntity(Department department);

    default Department toDomain(DepartmentEntity entity) {
        if (entity == null) {
            return null;
        }

        return Department.reconstitute(
                entity.getId(),
                entity.getFacilityId(),
                entity.getCode(),
                entity.getName(),
                entity.isActive()
        );
    }
}