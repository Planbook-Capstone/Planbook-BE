package com.BE.service.interfaceServices;

import com.BE.enums.AcademicYearStatusEnum;
import com.BE.model.entity.AcademicYear;
import com.BE.model.entity.User;
import com.BE.model.entity.WorkSpace;
import com.BE.model.request.AcademicYearRequest;
import com.BE.model.response.AcademicYearResponse;

import java.util.List;
import java.util.UUID;

public interface IAcademicYearService {
    List<AcademicYearResponse> getAll();

    AcademicYearResponse create(AcademicYearRequest request);

    AcademicYearResponse update(UUID id, AcademicYearRequest request);

    AcademicYearResponse updateStatus(UUID id, AcademicYearStatusEnum status);

    void delete(UUID id);

    AcademicYear getActiveAcademicYear();

    WorkSpace createWorkspaceForNewUser(User auth);

}