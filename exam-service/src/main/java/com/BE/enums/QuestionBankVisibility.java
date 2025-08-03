package com.BE.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Enum representing the visibility level of question banks
 */
@Getter
@Schema(description = "Question bank visibility levels")
public enum QuestionBankVisibility {
    
    @Schema(description = "Public - visible to all users (created by staff)")
    PUBLIC("PUBLIC", "Công khai - Tất cả người dùng có thể xem"),
    
    @Schema(description = "Private - only visible to creator (created by non-staff users)")
    PRIVATE("PRIVATE", "Riêng tư - Chỉ người tạo có thể xem");

    private final String code;
    private final String description;

    QuestionBankVisibility(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Check if this visibility allows public access
     */
    public boolean isPublic() {
        return this == PUBLIC;
    }

    /**
     * Check if this visibility is private
     */
    public boolean isPrivate() {
        return this == PRIVATE;
    }

    /**
     * Get visibility based on user role
     * @param isStaff true if user is staff, false otherwise
     * @return PUBLIC if staff, PRIVATE if not staff
     */
    public static QuestionBankVisibility getByUserRole(boolean isStaff) {
        return isStaff ? PUBLIC : PRIVATE;
    }
}
