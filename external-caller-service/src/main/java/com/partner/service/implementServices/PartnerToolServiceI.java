package com.partner.service.implementServices;

import com.partner.model.request.ToolExecuteRequest;
import com.partner.service.TokenService;
import com.partner.service.interfaceServices.IPartnerToolService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartnerToolServiceI implements IPartnerToolService {

    TokenService tokenService;
     WebClient.Builder webClient;


    @Override
    public Mono<Map<String, Object>> execute(ToolExecuteRequest input) {
        return tokenService.getAccessToken(
                        input.getToolName(),
                        input.getTokenUrl(),
                        input.getClientId(),
                        input.getClientSecret()
                )
                .flatMap(token -> webClient.build()
                        .post()
                        .uri(input.getApiUrl())
                        .headers(h -> h.setBearerAuth(token))
                        .bodyValue(input.getPayload())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                );
    }


}

