package com.BE.controller;

import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserResponse;
import com.BE.service.interfaceServices.IUserService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {


    IUserService userService;
    ResponseHandler responseHandler;


    @PutMapping("{id}")
    @Operation(
            summary = "Cập nhật hồ sơ người dùng",
            description = "Cập nhật thông tin hồ sơ của người dùng dựa trên ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật hồ sơ thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy hồ sơ người dùng")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Dữ liệu cập nhật hồ sơ người dùng",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Ví dụ cập nhật hồ sơ",
                                    summary = "Cập nhật hồ sơ người dùng",
                                    value = """
                                            {
                                              "fullName": "Nguyễn Văn A",
                                              "phone": "0912345678",
                                              "avatar": "https://example.com/avatarB.jpg",
                                              "birthday": "15-08-1998",
                                              "gender": "FEMALE"
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity update(@PathVariable UUID id,
                                 @RequestBody @Valid UserProfileRequest request) {
        return responseHandler.response(200, "Cập nhật hồ sơ thành công", userService.update(id, request));
    }

}
