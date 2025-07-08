package com.partner.service.interfaceServices;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface IPartnerToolService {
     Mono<String> executeTool(Long toolId, Map<String, Object> input);
}
