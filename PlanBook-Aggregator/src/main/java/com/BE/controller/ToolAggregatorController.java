package com.BE.controller;

import com.BE.model.request.ToolExecuteRequest;
import com.BE.service.interfaceServices.IToolAggregatorService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
