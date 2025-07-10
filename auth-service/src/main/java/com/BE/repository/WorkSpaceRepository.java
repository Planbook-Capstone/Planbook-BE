package com.BE.repository;

import com.BE.model.entity.AuthUser;
import com.BE.model.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.BE.model.entity.AcademicYear;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, UUID> {
    boolean existsByAuthAndAcademicYear(AuthUser auth, AcademicYear academicYear);
}