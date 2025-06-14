package com.BE.controller;


import com.BE.model.entity.User;
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
@SecurityRequirement(name ="api")
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
        return "Message '" + message + "' sent to Kafka!";
    }

    @PostMapping("/refresh")
    public ResponseEntity refresh( @RequestBody RefreshRequest refreshRequest){
        return responseHandler.response(200, "Refresh Token success!", iAuthenticationService.refresh(refreshRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody RefreshRequest refreshRequest){
        iAuthenticationService.logout(refreshRequest);
        return ResponseEntity.ok( "Logout success!");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody AuthenticationRequest user){
        return responseHandler.response(200, "Register success!", iAuthenticationService.register(user));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns authentication tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Missing or invalid fields")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thông tin đăng nhập của người dùng (username và password).",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequestDTO.class),
                    examples = {
                            @ExampleObject(
                                    name = "Admin Login Example",
                                    summary = "Đăng nhập với tài khoản Admin",
                                    value = "{\"username\": \"admin\", \"password\": \"admin\"}"
                            ),
                            @ExampleObject(
                                    name = "Teacher Login Example",
                                    summary = "Đăng nhập với tài khoản Teacher",
                                    value = "{\"username\": \"teacher\", \"password\": \"teacher\"}"
                            ),
                            @ExampleObject(
                                    name = "Staff Login Example",
                                    summary = "Đăng nhập với tài khoản Staff",
                                    value = "{\"username\": \"staff\", \"password\": \"staff\"}"
                            )
                    }
            )
    )
    public  ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return responseHandler.response(200, "Login success!", iAuthenticationService.authenticate(loginRequestDTO));
    }

    @PostMapping("/login-google")
    public ResponseEntity checkLoginGoogle(@RequestBody LoginGoogleRequest loginGGRequest){
        return responseHandler.response(200, "Login Google success!", iAuthenticationService.loginGoogle(loginGGRequest));
    }

    @PostMapping("forgot-password")
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        iAuthenticationService.forgotPasswordRequest(forgotPasswordRequest.getEmail());
        return ResponseEntity.ok( "Forgot Password successfully");
    }

    @PatchMapping("reset-password")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        iAuthenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok( "Reset Password successfully");
    }

    @GetMapping("/testRole")
    public ResponseEntity testRole(){
        return ResponseEntity.ok("Test Role User Successfully");
    }


    @GetMapping("/git-action")
    public ResponseEntity hihi(){
        return ResponseEntity.ok("Git-action Successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity admin(){
        return ResponseEntity.ok(iAuthenticationService.admin());
    }

    @PatchMapping("/status")
    public ResponseEntity status(@Valid @RequestBody StatusRequest statusRequest) {
        return ResponseEntity.ok(statusRequest.getStatus());
    }




}
