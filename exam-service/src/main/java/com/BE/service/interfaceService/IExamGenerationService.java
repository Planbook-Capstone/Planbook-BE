package com.BE.service.interfaceService;

import com.BE.model.request.ExamGenerationRequest;

import java.util.List;
import java.util.Map;

public interface IExamGenerationService {
    List<Map<String, Object>> generateExams(ExamGenerationRequest request);
}
