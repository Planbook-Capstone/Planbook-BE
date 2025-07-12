//package com.BE.feign;
//
//import com.BE.model.response.BookTypeResponse;
//import com.BE.model.response.DataResponseDTO;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.UUID;
//
//@FeignClient(name = "auth-service")
//public interface ToolInternalServiceClient {
//
//
//
//    @GetMapping("/{id}")
//    DataResponseDTO<BookTypeResponse> getBookTypeById(@PathVariable("id") UUID id);
//
//}
