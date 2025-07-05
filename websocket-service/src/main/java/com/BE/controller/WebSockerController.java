package com.BE.controller;


import com.BE.model.request.WebSocketMessageRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/websocker")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class WebSockerController {

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @PostMapping("/push")
    public void pushToClient(@RequestBody WebSocketMessageRequest request) {
        messagingTemplate.convertAndSendToUser(
                request.getUserId(),
                request.getDestination(),
                request.getPayload()
        );
    }

}
