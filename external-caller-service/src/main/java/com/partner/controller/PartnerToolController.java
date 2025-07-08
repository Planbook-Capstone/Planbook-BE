package com.partner.controller;

import com.partner.model.request.ToolExecuteRequest;
import com.partner.model.response.DataResponseDTO;
import com.partner.service.interfaceServices.IPartnerToolService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/partner-tools")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartnerToolController {
    IPartnerToolService service;

    @PostMapping("/execute")
    public Mono<ResponseEntity<DataResponseDTO<String>>> executeExternalTool(
             @RequestBody ToolExecuteRequest request) {
        return service.execute(request)
                .map(output -> ResponseEntity.ok(
                        DataResponseDTO.<String>builder()
                                .statusCode(200)
                                .message("Thực thi công cụ thành công.")
                                .data(output)
                                .build()
                ))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body(
                        DataResponseDTO.<String>builder()
                                .statusCode(500)
                                .message("Lỗi khi gọi công cụ: " + e.getMessage())
                                .data(null)
                                .build()
                )));
    }


}
