package com.BE.enums;

public enum StatusEnum {
    PENDING,        // Đang chờ thanh toán
    PAID,           // Đã thanh toán thành công (thay cho SUCCESS)
    FAILED,         // Thất bại
    CANCELLED,      // Đã hủy
    EXPIRED,        // Đã hết hạn
    RETRY // Đã thử thanh toán lại (tùy chọn, để theo dõi)

}
