package com.BE.service.interfaceServices;

import java.util.Map;

public interface IEmailService {
    void sendTemplateEmail(String toEmail, String templateId, Map<String, String> dynamicData);
}
