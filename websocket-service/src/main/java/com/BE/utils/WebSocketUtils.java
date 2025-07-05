package com.BE.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketUtils {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketUtils(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Gửi dữ liệu đến client qua WebSocket sử dụng topic cụ thể
     * @param destination: topic định dạng /topic/something hoặc /queue/user
     * @param payload: dữ liệu muốn gửi
     */
    public void sendToTopic(String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }

    /**
     * Gửi dữ liệu đến một user cụ thể (point-to-point)
     * @param username: tên user (được xác định từ principal name)
     * @param destination: ví dụ: "/queue/notification"
     * @param payload: dữ liệu muốn gửi
     */
    public void sendToUser(String username, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }
}