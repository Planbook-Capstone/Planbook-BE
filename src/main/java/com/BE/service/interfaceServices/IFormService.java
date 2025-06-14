package com.BE.service.interfaceServices;

import com.BE.model.entity.Form;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface IFormService {

    Form saveForm(JsonNode jsonNode);

    Map<String, Object> getForm(Long id);
}
