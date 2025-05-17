package com.BE.controller;


import com.BE.model.entity.User;
import com.BE.model.request.*;
import com.BE.model.response.AuthenticationResponse;
import com.BE.service.interfaceServices.IAuthenticationService;
import com.BE.utils.ResponseHandler;
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
