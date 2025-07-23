package com.BE.enums;

public enum PaymentStatusEnum {
    PENDING,        // Đang chờ thanh toán
    PAID,           // Đã thanh toán thành công (thay cho SUCCESS)
    FAILED,         // Thất bại
    CANCELLED,      // Đã hủy
    EXPIRED,        // Đã hết hạn
    RETRY_ATTEMPTED // Đã thử thanh toán lại (tùy chọn, để theo dõi)

}
