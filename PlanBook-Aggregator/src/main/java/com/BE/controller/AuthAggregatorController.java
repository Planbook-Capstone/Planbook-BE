//package com.BE.controller;
//
//import com.BE.model.request.RegisterAggregatorRequest;
//import com.BE.service.implementServices.AuthAggregatorServiceImpl;
//import com.BE.utils.ResponseHandler;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import jakarta.validation.Valid;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//// AggregatorController.java
//@RestController
//@RequestMapping("api/auth")
//@RequiredArgsConstructor
//@SecurityRequirement(name = "api")
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class AuthAggregatorController {
//
//    AuthAggregatorServiceImpl aggregatorService;
//    ResponseHandler responseHandler;
//
//    @PostMapping("/register")
//    @Operation(summary = "Đăng ký tài khoản và hồ sơ người dùng")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
//            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
//    })
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            required = true,
//            content = @Content(
//                    schema = @Schema(implementation = RegisterAggregatorRequest.class),
//                    examples = {
//                            @ExampleObject(
//                                    name = "RegisterExample",
//                                    summary = "Ví dụ đăng ký",
//                                    value = """
//                                            {
//                                              "email": "user@gmail.com",
//                                              "username": "user123",
//                                              "password": "secret123",
//                                              "fullName": "Nguyễn Văn A"
//                                            }
//                                            """
//                            )
//                    }
//            )
//    )
//    public ResponseEntity register(
//            @Valid @RequestBody RegisterAggregatorRequest request
//    ) {
//
//        return responseHandler.response(200, "Đăng ký thành công!", aggregatorService.register(request));
//    }
//}
