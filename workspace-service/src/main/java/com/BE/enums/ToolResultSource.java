package com.BE.enums;

public enum ToolResultSource {
    AI,             // Sinh ra từ công cụ AI (slide generator, lesson plan, etc.)
    USER_UPLOAD,    // Do teacher tự tạo hoặc upload lên (bản thân họ nhập liệu)
    SYSTEM          // Do hệ thống nội bộ tạo tự động (cron job, migration, import,...)
}
