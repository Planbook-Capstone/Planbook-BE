package com.BE.enums;

/**
 * Enum representing different types of lesson plan nodes
 */
public enum NodeType {
    SECTION,        // I, II, III, IV - Các phần chính
    SUBSECTION,     // 1, 2, 3, a, b, c, d - Các phần con
    LIST_ITEM,      // Các dấu gạch đầu dòng
    PARAGRAPH       // Nội dung chi tiết, văn bản
}
