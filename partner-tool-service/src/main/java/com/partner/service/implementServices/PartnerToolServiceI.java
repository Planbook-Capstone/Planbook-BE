package com.partner.service.implementServices;

import com.partner.model.entity.PartnerTool;
import com.partner.repository.PartnerToolRepository;
import com.partner.service.TokenService;
import com.partner.service.interfaceServices.IPartnerToolService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;



@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartnerToolServiceI implements IPartnerToolService {

    PartnerToolRepository repository;
    TokenService tokenService;

    @Override
    public Mono<String> executeTool(Long toolId, Map<String, Object> input) {
        return repository.findById(toolId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Không tìm thấy công cụ với ID: " + toolId)))
                .flatMap(tool ->
                        tokenService.getAccessToken(tool.getName(), tool.getTokenUrl(), tool.getClientId(), tool.getClientSecret())
                                .flatMap(token -> WebClient.create()
                                        .post()
                                        .uri(tool.getApiUrl())
                                        .headers(headers -> headers.setBearerAuth(token))
                                        .bodyValue(input)
                                        .retrieve()
                                        .bodyToMono(String.class)
                                )
                );
    }
}

