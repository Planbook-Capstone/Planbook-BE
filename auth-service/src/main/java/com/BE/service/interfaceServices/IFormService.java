package com.BE.service.interfaceServices;

import com.BE.model.entity.Form;
import com.BE.model.request.FormRequest;
import com.BE.model.response.FormResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public interface IFormService {

    Form saveForm(FormRequest formRequest);

    FormResponse getForm(Long id);

    List<FormResponse> getAllForms();
}
