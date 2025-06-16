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
}
