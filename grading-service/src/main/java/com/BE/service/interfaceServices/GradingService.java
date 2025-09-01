package com.BE.service.interfaceServices;

import com.BE.model.entity.StudentSubmission;
import com.BE.model.request.StudentSubmissionRequest;

public interface GradingService {
    /**
     * Grades a student's submission against the correct answer key.
     *
     * @param submissionRequest The student's submission data.
     * @return A fully graded StudentSubmission object with score, correctness, and points awarded.
     */
    StudentSubmission gradeSubmission(StudentSubmissionRequest submissionRequest);
}

