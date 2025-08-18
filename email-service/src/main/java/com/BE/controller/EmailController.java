package com.BE.controller;


import com.BE.model.request.EmailDataRequest;
import com.BE.service.interfaceServices.IEmailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class EmailController {

    private final IEmailService iEmailService;


    @PostMapping("/send")
    public ResponseEntity pushToClient(@Valid @RequestBody EmailDataRequest request) {
        iEmailService.sendTemplateEmail(request.getToEmail(),request.getTemplateId(),request.getDynamicData());
       return ResponseEntity.ok("Gửi mail bằng template với id: " + request.getTemplateId() + " thành công");
    }

}
