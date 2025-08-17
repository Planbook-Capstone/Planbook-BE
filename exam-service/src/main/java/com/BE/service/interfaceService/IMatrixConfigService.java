package com.BE.service.interfaceService;

import com.BE.enums.StatusEnum;
import com.BE.model.request.MatrixConfigRequest;
import com.BE.model.response.MatrixConfigResponse;

import java.util.List;

public interface IMatrixConfigService {
    MatrixConfigResponse create(MatrixConfigRequest request);

    MatrixConfigResponse update(Long id, MatrixConfigRequest request);

    MatrixConfigResponse updateStatus(Long id, StatusEnum status);

    List<MatrixConfigResponse> getAllByStatus(StatusEnum status);

    MatrixConfigResponse getById(Long id);

}
