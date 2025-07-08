package com.BE.service.interfaceServices;

import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.response.ExternalToolConfigResponse;

import java.util.UUID;

public interface IExternalToolConfigService {
   ExternalToolConfigResponse create(ExternalToolConfigRequest request, UUID createdBy);
}
