package com.BE.controller;

import com.BE.enums.GenderEnum;
import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import com.BE.model.request.CreateUserRequest;
import com.BE.model.request.StatusRequest;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserResponse;
import com.BE.service.interfaceServices.IUserService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PostMapping
    @Operation(
            summary = "Tạo mới người dùng",
            description = "Chỉ dành cho Admin. Tạo tài khoản mới với thông tin cơ bản và phân quyền vai trò."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo mới người dùng thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tài khoản đã tồn tại")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin người dùng cần tạo",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateUserRequest.class),
                    examples = @ExampleObject(
                            name = "Ví dụ tạo người dùng",
                            value = """
                                    {
                                      "fullName": "partner",
                                      "username": "partner",
                                      "password": "partner",
                                      "email": "partner@gmail.com",                     
                                      "role": "PARTNER"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest request) {
        return responseHandler.response(201, "Tạo mới người dùng thành công", userService.create(request));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Cập nhật trạng thái người dùng",
            description = "Chỉ dành cho Admin. Cho phép thay đổi trạng thái tài khoản của người dùng (ACTIVE, INACTIVE, BANNED)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @Valid @RequestBody StatusRequest request) {
        return responseHandler.response(200,"Cập nhật trạng thái người dùng thành công", userService.updateStatus(id, request.getStatus()));
    }


    @Operation(
            summary = "Lấy danh sách người dùng với tìm kiếm và lọc",
            description = "Tìm kiếm theo tên (`search`), lọc theo vai trò (`role`), trạng thái (`status`), giới tính (`gender`) và sắp xếp theo `createdAt` hoặc `updatedAt` theo chiều `asc|desc`.",
            parameters = {
                    @Parameter(name = "search", description = "Từ khóa tìm kiếm theo họ tên"),
                    @Parameter(name = "role", description = "Vai trò người dùng", schema = @Schema(implementation = RoleEnum.class)),
                    @Parameter(name = "status", description = "Trạng thái người dùng", schema = @Schema(implementation = StatusEnum.class)),
                    @Parameter(name = "gender", description = "Giới tính", schema = @Schema(implementation = GenderEnum.class)),
                    @Parameter(name = "sortBy", description = "Trường sắp xếp", schema = @Schema(allowableValues = {"createdAt", "updatedAt"}), example = "createdAt"),
                    @Parameter(name = "sortDirection", description = "Chiều sắp xếp", schema = @Schema(allowableValues = {"asc", "desc"}), example = "desc"),
                    @Parameter(name = "offset", description = "Số trang (bắt đầu từ 1)", schema = @Schema(example = "1")),
                    @Parameter(name = "pageSize", description = "Số phần tử mỗi trang", schema = @Schema(example = "10"))
            }
    )
    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(required = false) StatusEnum status,
            @RequestParam(required = false) GenderEnum gender,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "1") int offset,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return responseHandler.response(200, "Danh sách người dùng", userService.getUsersWithFilter(
                        search, role, status, gender, offset, pageSize, sortBy, sortDirection
                ));
    }

}
