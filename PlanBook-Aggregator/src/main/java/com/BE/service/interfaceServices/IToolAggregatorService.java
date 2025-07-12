package com.BE.service.interfaceServices;

import com.BE.model.request.ToolExecuteRequest;

public interface IToolAggregatorService {
    String executeExternalTool(ToolExecuteRequest request);

    String executeInternalTool(ToolExecuteRequest request);
}
