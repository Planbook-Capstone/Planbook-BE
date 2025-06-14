package com.BE.service.implementServices;

import com.BE.exception.exceptions.BadRequestException;
import com.BE.model.entity.Form;
import com.BE.repository.FormRepository;
import com.BE.service.interfaceServices.IFormService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormServiceImpl implements IFormService {
    @Autowired
    FormRepository formRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public Form saveForm(JsonNode jsonNode) {
        Form form = new Form();
        try {
            String jsonString = objectMapper.writeValueAsString(jsonNode.get("formDefinition"));
            form.setFormDefinition(jsonString);
            return formRepository.save(form);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Error converting form definition to JSON");
        }

    }

    public Map<String, Object> getForm(Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        Map<String, Object> response = new HashMap<>();
        try {
            response.put("formDefinition", objectMapper.readValue(form.getFormDefinition(), Object.class));
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing form definition JSON", e);
        }
        // Parse lại JSON string thành object để trả ra đúng format

    }
}
