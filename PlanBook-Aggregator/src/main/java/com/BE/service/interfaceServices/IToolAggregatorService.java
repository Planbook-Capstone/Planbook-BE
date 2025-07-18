package com.BE.service.interfaceServices;

import com.BE.model.request.ToolExecuteRequest;
import com.BE.model.request.ToolSearchPageRequest;
import com.BE.model.response.AggregatedToolResponse;

import java.util.Map;

public interface IToolAggregatorService {
    Map<String,Object>  executeExternalTool(ToolExecuteRequest request);

    String executeInternalTool(ToolExecuteRequest request);
    AggregatedToolResponse getAggregatedToolInfo(ToolSearchPageRequest request);
}
