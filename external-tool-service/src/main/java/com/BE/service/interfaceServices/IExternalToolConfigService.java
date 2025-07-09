package com.BE.service.interfaceServices;

import com.BE.enums.StatusEnum;
import com.BE.model.request.ExternalToolConfigRequest;
import com.BE.model.request.ExternalToolSearchRequest;
import com.BE.model.response.ExternalToolConfigResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IExternalToolConfigService {
   ExternalToolConfigResponse create(ExternalToolConfigRequest request);

   Page<ExternalToolConfigResponse> getAll(ExternalToolSearchRequest request);

   ExternalToolConfigResponse getById(Long id);
   ExternalToolConfigResponse update(Long id, ExternalToolConfigRequest request);
   ExternalToolConfigResponse updateStatus(Long id, StatusEnum status);

}
