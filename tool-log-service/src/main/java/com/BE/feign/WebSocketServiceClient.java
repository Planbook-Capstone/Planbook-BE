package com.BE.feign;

import com.BE.model.request.WebSocketMessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "websocket-service")
public interface WebSocketServiceClient {

        @PostMapping("/api/web-socket/push")
        void pushToClient(@RequestBody WebSocketMessageRequest request);
}
