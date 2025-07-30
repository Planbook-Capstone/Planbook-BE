package com.BE.service.interfaceService;

import org.springframework.core.io.Resource;

import java.util.UUID;

public interface IExcelService {
    
    Resource generateExcelReport(UUID examInstanceId);
    
    void updateExcelUrl(UUID examInstanceId, String excelUrl);
}
