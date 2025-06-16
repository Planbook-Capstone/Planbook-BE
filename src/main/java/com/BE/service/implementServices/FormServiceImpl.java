package com.BE.service.implementServices;

import com.BE.exception.exceptions.BadRequestException;
import com.BE.mapper.FormMapper;
import com.BE.model.entity.Form;
import com.BE.model.request.FormRequest;
import com.BE.model.response.FormResponse;
import com.BE.repository.FormRepository;
import com.BE.service.interfaceServices.IFormService;
import com.BE.utils.DateNowUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormServiceImpl implements IFormService {
    @Autowired
    FormRepository formRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    FormMapper formMapper;

    @Override
    public Form saveForm(FormRequest formRequest) {
        Form form = new Form();
        try {
            String jsonString = objectMapper.writeValueAsString(formRequest.getFormData());
            form.setName(formRequest.getName());
            form.setDescription(formRequest.getDescription());
            form.setFormDefinition(jsonString);
            form.setStatus(formRequest.getStatus());
            form.setCreatedAt(dateNowUtils.dateNow());
            form.setUpdatedAt(dateNowUtils.dateNow());
            return formRepository.save(form);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Error converting form definition to JSON");
        }
    }

    @Override
    public FormResponse getForm(Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));

        return formMapper.toFormResponse(form);
    }

    @Override
    public List<FormResponse> getAllForms() {
        List<Form> forms = formRepository.findAll();
        return formMapper.toFormResponseList(forms);
    }

    @Override
    public FormResponse updateForm(Long id, FormRequest formRequest) {
        Form existingForm = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found with id: " + id));

        try {
            // Convert formData to JSON string
            String jsonString = objectMapper.writeValueAsString(formRequest.getFormData());

            // Update form fields
            existingForm.setName(formRequest.getName());
            existingForm.setDescription(formRequest.getDescription());
            existingForm.setFormDefinition(jsonString);
            existingForm.setStatus(formRequest.getStatus());
            existingForm.setUpdatedAt(dateNowUtils.dateNow());

            // Save updated form and convert to response using mapper
            Form updatedForm = formRepository.save(existingForm);
            return formMapper.toFormResponse(updatedForm);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Error converting form definition to JSON");
        }
    }
}
