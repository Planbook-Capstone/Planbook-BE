package com.BE.controller;


import com.BE.model.request.*;
import com.BE.model.response.AuthenticationResponse;
import com.BE.service.interfaceServices.IAuthenticationService;
import com.BE.utils.KafkaMessageProducer;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name = "api")
public class AuthenticationController {

    @Autowired
    IAuthenticationService iAuthenticationService;

    @Autowired
    ResponseHandler responseHandler;

    @Autowired
    KafkaMessageProducer kafkaMessageProducer;

    @GetMapping("/sendMessage")
    public String sendMessageToKafka(@RequestParam("message") String message) {
        kafkaMessageProducer.sendMessage(message);
        return "Đã gửi tin nhắn '" + message + "' tới Kafka!";
    }

    @PostMapping("/refresh")
    public ResponseEntity refresh(@RequestBody RefreshRequest refreshRequest) {
        return responseHandler.response(200, "Làm mới Token thành công!", iAuthenticationService.refresh(refreshRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshRequest refreshRequest) {
        iAuthenticationService.logout(refreshRequest);
        return ResponseEntity.ok("Đăng xuất thành công!");
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody AuthenticationRequest user) {
        return responseHandler.response(200, "Đăng ký thành công!", iAuthenticationService.register(user));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập người dùng", description = "Xác thực người dùng và trả về token xác thực.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Không được phép - Sai thông tin đăng nhập"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ - Thiếu hoặc sai trường dữ liệu")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin đăng nhập của người dùng (username và password).",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequestDTO.class),
                    examples = {
                            @ExampleObject(
                                    name = "Ví dụ đăng nhập Admin",
                                    summary = "Đăng nhập với tài khoản Admin",
                                    value = "{\"username\": \"admin\", \"password\": \"admin\"}"
                            ),
                            @ExampleObject(
                                    name = "Ví dụ đăng nhập Giáo viên",
                                    summary = "Đăng nhập với tài khoản Giáo viên",
                                    value = "{\"username\": \"teacher\", \"password\": \"teacher\"}"
                            ),
                            @ExampleObject(
                                    name = "Ví dụ đăng nhập Nhân viên",
                                    summary = "Đăng nhập với tài khoản Nhân viên",
                                    value = "{\"username\": \"staff\", \"password\": \"staff\"}"
                            ),
                            @ExampleObject(
                                    name = "Ví dụ đăng nhập Nhân viên",
                                    summary = "Đăng nhập với tài khoản Quản lí tool",
                                    value = "{\"username\": \"partner\", \"password\": \"partner\"}"
                            )
                    }
            )
    )
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return responseHandler.response(200, "Đăng nhập thành công!", iAuthenticationService.authenticate(loginRequestDTO));
    }

    @PostMapping("/login-google")
    public ResponseEntity checkLoginGoogle(@RequestBody LoginGoogleRequest loginGGRequest) {
        return responseHandler.response(200, "Đăng nhập Google thành công!", iAuthenticationService.loginGoogle(loginGGRequest));
    }

    @PostMapping("forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        iAuthenticationService.forgotPasswordRequest(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok("Yêu cầu quên mật khẩu thành công");
    }

    @PatchMapping("reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        iAuthenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công");
    }

    @GetMapping("/testRole")
    public ResponseEntity testRole() {
        return ResponseEntity.ok("Kiểm tra vai trò người dùng thành công");
    }


    @GetMapping("/git-action")
    public ResponseEntity hihi() {
        return ResponseEntity.ok("git-action thành công");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity admin() {
        return ResponseEntity.ok(iAuthenticationService.admin());
    }




}