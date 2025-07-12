package com.BE.enums;

public enum ExecutionStatus {

    PENDING,    // Mới tạo log, chưa xử lý
    SUCCESS,    // Gọi tool thành công
    FAILED,     // Có lỗi xảy ra
    TIMEOUT,    // Tool không phản hồi
    INVALID_INPUT, // Dữ liệu vào không hợp lệ
    UNAUTHORIZED // Gọi không có quyền
}
