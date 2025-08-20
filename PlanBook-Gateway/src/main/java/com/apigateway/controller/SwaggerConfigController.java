package com.apigateway.controller; // Hoặc package tương ứng của bạn trên Gateway

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SwaggerConfigController {

    @Autowired
    private DiscoveryClient discoveryClient;

    // Endpoint này phải khớp với `springdoc.swagger-ui.config-url` trong properties
    @GetMapping("/v3/api-docs/swagger-config")
    public ResponseEntity<Map<String, Object>> swaggerConfig() {
        Map<String, Object> swaggerConfig = new LinkedHashMap<>();
        List<Map<String, String>> urls = new ArrayList<>();

        // Lấy danh sách tất cả các service từ Zookeeper
        List<String> services = discoveryClient.getServices();

        services.stream()
                // Lọc ra những service không cần hiển thị docs (ví dụ như chính gateway)
                .filter(serviceId -> !serviceId.equalsIgnoreCase("api-gateway"))
                .forEach(serviceId -> {
                    Map<String, String> url = new HashMap<>();
                    // URL sẽ có dạng /<service-id>/v3/api-docs
                    // ví dụ: /auth-service/v3/api-docs
                    url.put("url", "/" + serviceId.toLowerCase() + "/v3/api-docs");
//                    url.put("url", "https://planbook.vn/" + serviceId.toLowerCase() + "/v3/api-docs");
                    url.put("name", serviceId);

                    urls.add(url);
                });

        swaggerConfig.put("urls", urls);
        return new ResponseEntity<>(swaggerConfig, HttpStatus.OK);
    }

}