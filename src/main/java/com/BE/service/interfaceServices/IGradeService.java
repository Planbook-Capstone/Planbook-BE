package com.BE.service.interfaceServices;

import com.BE.model.request.GradeRequest;
import com.BE.model.response.GradeResponse;

public interface IGradeService {
    GradeResponse createGrade(GradeRequest request);
}
