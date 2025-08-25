package com.BE.model.request;

import com.BE.enums.ToolCodeEnum;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

@Schema(description = "Dữ liệu gửi lên để cấu hình công cụ bên thứ ba (External Tool)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalToolConfigRequest {

    @NotBlank(message = "Tên công cụ không được để trống")
    @Schema(
            description = "Tên định danh duy nhất của công cụ bên ngoài. Dùng để phân biệt các tool.",
            example = "Google Translate",
            required = true
    )
    String name;

    @NotBlank(message = "API URL không được để trống")
    @Schema(
            description = "Địa chỉ endpoint của API chính mà hệ thống sẽ gọi đến để sử dụng công cụ.",
            example = "https://api.partner.com/translate",
            required = true
    )
    String apiUrl;

    @NotBlank(message = "Token URL không được để trống")
    @Schema(
            description = "URL cấp token theo chuẩn OAuth2 (client credentials) để truy cập công cụ.",
            example = "https://partner.com/oauth2/token",
            required = true
    )
    String tokenUrl;

    @NotBlank(message = "Client ID không được để trống")
    @Schema(
            description = "Client ID dùng để xác thực khi lấy token truy cập.",
            example = "partner-client-id",
            required = true
    )
    String clientId;

    @NotBlank(message = "Client Secret không được để trống")
    @Schema(
            description = "Client Secret tương ứng với clientId, dùng để lấy token truy cập.",
            example = "partner-client-secret",
            required = true
    )
    String clientSecret;

    @Schema(
            description = "Icon đại diện cho công cụ hỗ trợ, có thể là base64 encoded string", // ĐÃ THAY ĐỔI MÔ TẢ
            example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==",
            type = "string",
            nullable = true
    )
    String icon;


    @Schema(description = "Dữ liệu JSON đầu vào cho tool", example = "{\"query\": \"hello\"}")
    @NotNull(message = "Trường inputJson không được để trống")
    Map<String, Object> inputJson;

    @NotNull(message = "Số lượng token bị trừ mỗi khi người dùng gọi một truy vấn (query).")
    @Min(value = 1, message = "Giá trị tối thiểu: 1.")
    @Max(value = 1000, message = "Giá trị tối đa đề xuất: 1000.")
    Integer tokenCostPerQuery;


    @Schema(
            description = "Mô tả ngắn gọn về công cụ, có thể dùng để ghi chú hoặc hiển thị cho người dùng.",
            example = "Dịch ngôn ngữ thời gian thực bằng Google"
    )
    String description;

    @EnumValidator(enumClass = ToolCodeEnum.class, message = "Code không hợp lệ")
    @Schema(description = "Mã định danh cố định của tool, dùng để mapping ", example = "TOOL", required = true)
    ToolCodeEnum code;

    String href;
}

