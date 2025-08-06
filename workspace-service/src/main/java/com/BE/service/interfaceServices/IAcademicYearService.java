package com.BE.service.interfaceServices;

import com.BE.enums.AcademicYearStatusEnum;
import com.BE.model.entity.AcademicYear;
import com.BE.model.request.AcademicYearRequest;
import com.BE.model.response.AcademicYearResponse;

import java.util.List;
import java.util.UUID;

public interface IAcademicYearService {
    List<AcademicYearResponse> getAll();

    AcademicYearResponse create(AcademicYearRequest request);

    AcademicYearResponse update(Long id, AcademicYearRequest request);

    AcademicYearResponse updateStatus(Long id, AcademicYearStatusEnum status);

    void delete(Long id);

    AcademicYear getActiveAcademicYear();

//    WorkSpace createWorkspaceForNewUser(User auth);

}