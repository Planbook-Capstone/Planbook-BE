package com.BE.repository;

import com.BE.model.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
}