package com.BE.service.interfaceServices;

import com.BE.model.request.ToolExecuteRequest;

import java.util.Map;

public interface IToolAggregatorService {
    Map<String,Object>  executeExternalTool(ToolExecuteRequest request);

    String executeInternalTool(ToolExecuteRequest request);
}
