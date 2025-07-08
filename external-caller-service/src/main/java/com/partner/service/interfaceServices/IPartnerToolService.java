package com.partner.service.interfaceServices;

import com.partner.model.request.ToolExecuteRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface IPartnerToolService {
     Mono<String> execute(ToolExecuteRequest input);
}
