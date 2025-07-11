package com.BE.controller;

import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserProfileResponse;
import com.BE.service.interfaceServices.IUserProfileService;
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
@RequestMapping("/api/user-profile")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {


    IUserProfileService userProfileService;
    ResponseHandler responseHandler;

    @GetMapping("{id}")
    @Operation(summary = "Lấy hồ sơ người dùng", description = "Trả về thông tin hồ sơ người dùng dựa trên ID.")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return responseHandler.response(200, "Lấy thông tin thành công", userProfileService.getById(id));
    }

    @PostMapping("{id}")
    @Operation(
            summary = "Tạo hồ sơ người dùng",
            description = "Tạo thông tin hồ sơ người dùng dựa trên ID (trùng với user trong hệ thống auth)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo hồ sơ thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Dữ liệu hồ sơ người dùng",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserProfileRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "Ví dụ tạo hồ sơ",
                                    summary = "Tạo hồ sơ người dùng",
                                    value = """
                                            {
                                              "fullName": "Nguyễn Văn A",
                                              "phone": "0987654321",
                                              "avatar": "https://example.com/avatar.jpg",
                                              "birthday": "01-01-2000",
                                              "gender": "Nam"
                                            }
                                            """
                            )
                    }
            )
    )
    public ResponseEntity<UserProfileResponse> create(@PathVariable UUID id,
                                                      @RequestBody @Valid UserProfileRequest request) {
        return responseHandler.response(200, "Tạo hồ sơ thành công", userProfileService.create(id, request));
    }


    @PutMapping("{id}")
    @Operation(
            summary = "Cập nhật hồ sơ người dùng",
            description = "Cập nhật thông tin hồ sơ của người dùng dựa trên ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật hồ sơ thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
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
                                          "fullName": "Trần Thị B",
                                          "phone": "0912345678",
                                          "avatar": "https://example.com/avatarB.jpg",
                                          "birthday": "15-08-1998",
                                          "gender": "Nữ"
                                        }
                                        """
                            )
                    }
            )
    )
    public ResponseEntity<UserProfileResponse> update(@PathVariable UUID id,
                                                      @RequestBody @Valid UserProfileRequest request) {
        return responseHandler.response(200, "Cập nhật hồ sơ thành công", userProfileService.update(id, request));
    }


    @DeleteMapping("{id}")
    @Operation(summary = "Xóa hồ sơ người dùng", description = "Xóa hồ sơ người dùng dựa trên ID.")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        userProfileService.delete(id);
        return responseHandler.response(200, "Xóa hồ sơ thành công", null);
    }
}
