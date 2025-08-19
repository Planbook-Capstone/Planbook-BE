package com.BE.model.request;

import com.BE.enums.StatusEnum;
import com.BE.enums.TimeRangePreset;
import com.BE.enums.TransactionType;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionFilterRequest {

    @Schema(description = "UUID người dùng. Nếu không truyền, sẽ lấy từ token.", example = "a2b5c3e8-7f12-45d2-9f7f-0c83d2a81234")
    private UUID userId;

    @Schema(description = "Loại giao dịch", example = "RECHARGE", allowableValues = {"RECHARGE", "REFUND", "REWARD", "TOOL_USAGE"})
    @EnumValidator(enumClass = TransactionType.class, message = "Loại giao dịch không hợp lệ")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Schema(description = "Từ ngày (dd-MM-yyyy)", example = "01-01-2025", type = "string", format = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate fromDate;

    @Schema(description = "Đến ngày (dd-MM-yyyy)", example = "31-12-2025", type = "string", format = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate toDate;

    @Schema(description = "Preset thời gian (nếu có, override fromDate/toDate)",
            example = "THIS_MONTH",
            allowableValues = {"TODAY", "YESTERDAY", "LAST_7_DAYS", "LAST_30_DAYS", "THIS_WEEK", "LAST_WEEK", "THIS_MONTH", "LAST_MONTH", "THIS_YEAR"})
    @EnumValidator(enumClass = TimeRangePreset.class, message = "Preset thời gian (nếu có, override fromDate/toDate) không hợp lệ")
    @Enumerated(EnumType.STRING)
    private TimeRangePreset timeRange;

    @Schema(description = "Trang số (bắt đầu từ 1)", example = "1")
    private int page = 1;

    @Schema(description = "Số phần tử mỗi trang (1–100)", example = "10")
    private int size = 10;

    @Schema(description = "Trường sắp xếp", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Chiều sắp xếp (asc hoặc desc)", example = "desc", allowableValues = {"asc", "desc"})
    private String sortDir = "desc";
}

