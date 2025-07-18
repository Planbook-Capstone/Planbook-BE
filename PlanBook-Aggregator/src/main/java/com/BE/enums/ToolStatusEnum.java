package com.BE.enums;

public enum ToolStatusEnum {
    PENDING,     // Đang chờ duyệt
    APPROVED,    // Đã được duyệt nhưng chưa kích hoạt (nếu hệ thống có bước active riêng)
    ACTIVE,      // Đang hoạt động
    INACTIVE,    // Tạm ngừng hoạt động (vẫn còn trong hệ thống)
    REJECTED,    // Bị từ chối bởi admin
    CANCELLED,   // Người dùng hủy yêu cầu
    DELETED      // Đã xóa khỏi hệ thống (logic delete)
}
