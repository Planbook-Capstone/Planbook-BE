package com.BE.controller;

import com.BE.model.entity.Form;
import com.BE.service.interfaceServices.IFormService;
import com.BE.utils.ResponseHandler;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Form", description = "API for form Lesson Plan")
@RequestMapping("/api/form")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FormController {

    IFormService formService;

    ResponseHandler responseHandler;

    @PostMapping
    public ResponseEntity<Form> saveForm(@RequestBody JsonNode jsonNode) {
        return responseHandler.response(200, "Form save successfully!", formService.saveForm(jsonNode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getForm(@PathVariable Long id) {
        return ResponseEntity.ok(formService.getForm(id));
    }
}
