package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.GradeRequest;
import com.BE.model.request.StatusRequest;
import com.BE.model.response.GradeResponse;
import org.springframework.data.domain.Page;

public interface IGradeService {
    GradeResponse createGrade(GradeRequest request);
    Page<GradeResponse> getAllGrades(int page, int size, String search, String status, String sortBy, String sortDirection);
    GradeResponse getGradeById(long id);
    GradeResponse updateGrade(long id, GradeRequest request); // Để cập nhật tên
    GradeResponse changeGradeStatus(long id, String newStatus);
}
