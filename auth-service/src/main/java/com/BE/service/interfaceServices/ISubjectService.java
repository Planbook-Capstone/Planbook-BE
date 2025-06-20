package com.BE.service.interfaceServices;

import com.BE.model.request.SubjectRequest;
import com.BE.model.response.SubjectResponse;
import org.springframework.data.domain.Page;

public interface ISubjectService {

    SubjectResponse createSubject(SubjectRequest request);
    Page<SubjectResponse> getAllSubjects(int page, int size, String search, String status, String sortBy, String sortDirection);
    SubjectResponse getSubjectById(long id);
    SubjectResponse updateSubject(long id, SubjectRequest request);
    SubjectResponse changeSubjectStatus(long id, String newStatus);

    // API mới: Lấy danh sách Subject theo Grade ID
    Page<SubjectResponse> getSubjectsByGradeId(Long gradeId, int page, int size, String search, String status, String sortBy, String sortDirection);
}
