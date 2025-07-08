package com.partner.controller;

import com.partner.model.response.DataResponseDTO;
import com.partner.service.interfaceServices.IPartnerToolService;
import com.partner.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
    ResponseHandler responseHandler;

    @PostMapping("/{toolId}/execute")
    public Mono<ResponseEntity<DataResponseDTO<String>>> execute(@PathVariable Long toolId,
                                                                 @RequestBody Map<String, Object> input) {
        return service.executeTool(toolId, input)
                .map(result -> ResponseEntity.ok(
                        DataResponseDTO.<String>builder()
                                .statusCode(200)
                                .message("Thực thi thành công công cụ bên đối tác.")
                                .data(result)
                                .build()
                ))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(500).body(
                                DataResponseDTO.<String>builder()
                                        .statusCode(500)
                                        .message("Lỗi khi gọi công cụ đối tác: " + e.getMessage())
                                        .data(null)
                                        .build()
                        )
                ));
    }

}
