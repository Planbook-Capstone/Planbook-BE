package com.BE.service.implementServices;

import com.BE.exception.exceptions.EmailSendingException;
import com.BE.service.interfaceServices.IEmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IEmailServiceImpl implements IEmailService {

    @Value("${spring.sendgrid.api-key}")
    private String sendgridApiKey;

    @Value("${spring.sendgrid.from.email}")
    private String FROM_EMAIL; // đã verify trên SendGrid

    @Override
    public void sendTemplateEmail(String toEmail, String templateId, Map<String, String> dynamicData) {
        Email from = new Email(FROM_EMAIL);
        Email to = new Email(toEmail);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        dynamicData.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);


        } catch (IOException e) {
            throw new EmailSendingException("Lỗi khi gửi email", e);
        }
    }
}

