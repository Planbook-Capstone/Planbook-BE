package com.BE.service.interfaceServices;

import com.BE.model.request.StudentSubmissionRequest;
import com.BE.model.response.StudentSubmissionResponse;

public interface StudentSubmissionService {
    /**
     * Creates and grades a new student submission.
     *
     * @param request The student's submission data.
     * @return A response DTO with the graded submission details.
     */
        StudentSubmissionResponse createAndGradeSubmission(StudentSubmissionRequest request);

    /**
     * Retrieves a student submission by its ID.
     *
     * @param id The ID of the submission.
     * @return A response DTO with the submission details.
     */
    StudentSubmissionResponse getById(Long id);

    /**
     * Retrieves all student submissions for a given grading session.
     *
     * @param gradingSessionId The ID of the grading session.
     * @return A list of response DTOs with submission details.
     */
    java.util.List<StudentSubmissionResponse> getByGradingSessionId(Long gradingSessionId);
}

