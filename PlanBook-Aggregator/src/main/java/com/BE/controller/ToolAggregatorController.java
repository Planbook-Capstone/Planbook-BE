package com.BE.controller;

import com.BE.model.request.ToolExecuteRequest;
import com.BE.model.request.ToolSearchPageRequest;
import com.BE.model.response.AggregatedToolResponse;
import com.BE.model.response.BookTypeResponse;
import com.BE.model.response.ExternalToolConfigResponse;
import com.BE.service.interfaceServices.IToolAggregatorService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/tool")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ToolAggregatorController {

    IToolAggregatorService iToolAggregatorService;
    ResponseHandler responseHandler;


    @PostMapping("/execute")
    @Operation(
            summary = "Thực thi công cụ",
            description = "API dùng để thực thi một công cụ. Hệ thống sẽ phân loại công cụ là EXTERNAL hoặc INTERNAL dựa vào `toolType`, từ đó gọi đến dịch vụ tương ứng.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin thực thi công cụ",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ToolExecuteRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Thực thi thành công",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "ToolType không được hỗ trợ",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    public ResponseEntity executeTool(@Valid @RequestBody ToolExecuteRequest request) {
        switch (request.getToolType()) {
            case EXTERNAL:
                return responseHandler.response(200, "Thực thi thành công", iToolAggregatorService.executeExternalTool(request));
            case INTERNAL:
                return responseHandler.response(200, "Thực thi thành công", iToolAggregatorService.executeInternalTool(request));
            default:
                return responseHandler.response(404, "ToolType không được hỗ trợ: " + request.getToolType(), null);
        }
    }


    @GetMapping
    @Operation(
            summary = "Lấy danh sách công cụ và loại sách",
            description = """
        API này trả về danh sách các công cụ external và các loại sách nội bộ.

        - Danh sách công cụ (`externalTools`) hỗ trợ lọc theo nhiều trạng thái: `PENDING`, `APPROVED`, `ACTIVE`, `INACTIVE`, `REJECTED`, `CANCELLED`, `DELETED`.
        - Danh sách loại sách (`bookTypes`) **chỉ hỗ trợ lọc theo** `ACTIVE` và `INACTIVE`. 
        Nếu truyền giá trị khác (`PENDING`, `DELETED`,...) thì sẽ **không áp dụng lọc trạng thái cho bookTypes**.
        
        Mục đích là để frontend gọi 1 API duy nhất lấy dữ liệu tổng hợp.
    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thành công", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AggregatedToolResponse.class)
            ))
    })
    public ResponseEntity<?> getAggregatedToolInfo(@ParameterObject ToolSearchPageRequest request) {
        return responseHandler.response(
                200,
                "Lấy danh sách thành công",
                iToolAggregatorService.getAggregatedToolInfo(request)
        );
    }





}
