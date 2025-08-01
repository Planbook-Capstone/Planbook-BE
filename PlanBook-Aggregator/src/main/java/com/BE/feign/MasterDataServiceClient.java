package com.BE.feign;

import com.BE.config.FeignConfig;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.LessonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "master-data-service",  configuration = FeignConfig.class)
public interface MasterDataServiceClient {

    @GetMapping("/api/lessons/{id}")
    DataResponseDTO<LessonResponse> getLessonById(@PathVariable("id") long id);


}
