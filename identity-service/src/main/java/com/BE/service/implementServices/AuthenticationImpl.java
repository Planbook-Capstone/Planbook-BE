package com.BE.service.implementServices;

import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.AuthenException;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.InvalidRefreshTokenException;
import com.BE.feign.EmailServiceClient;
import com.BE.mapper.AuthMapper;
import com.BE.model.EmailDetail;
import com.BE.model.entity.User;
import com.BE.model.request.*;
import com.BE.model.response.AuthenResponse;
import com.BE.model.response.AuthenticationResponse;
import com.BE.repository.AuthenRepository;
import com.BE.service.EmailService;
import com.BE.service.JWTService;
import com.BE.service.RefreshTokenService;
import com.BE.service.interfaceServices.IAuthenticationService;
import com.BE.service.interfaceServices.IWalletService;
import com.BE.utils.AccountUtils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationImpl implements IAuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AuthenRepository authenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTService jwtService;

    @Autowired
    AuthMapper authMapper;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    IWalletService iWalletService;

    @Autowired
    EmailServiceClient emailServiceClient;

    @Value("${spring.sendgrid.template.reset.password}")
    private String templateResetPassword;

    @Value("${spring.link.reset.password}")
    private String linkResetPassword;

    @Value("${spring.sendgrid.template.verify}")
    private String templateVerifyUser;

    @Value("${spring.link.verify}")
    private String linkVerifyUser;
//
//    @Autowired
//    IAcademicYearService academicYearService;
//
//    @Autowired
//    IWorkSpaceService workSpaceService;

    @Value("${supabase.jwt.secret}")
    private String supabaseJwtSecret;


    public AuthenticationResponse register(AuthenticationRequest request) {
        User user = authMapper.toAuth(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(StatusEnum.INACTIVE);
        user.setRole(RoleEnum.TEACHER);
        try {
//            // Create workspace for new auth
//            WorkSpace ws = academicYearService.createWorkspaceForNewUser(user);
//            if (ws != null) {
//                user.getWorkSpaces().add(ws);
//                authenRepository.save(user);
//                workSpaceService.save(ws);
//            }
            iWalletService.create(user);
            user = authenRepository.save(user);
            sendEmailByTemplate(user, templateVerifyUser, linkVerifyUser);
            return authMapper.toAuthenticationResponse(user);
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            if(e.getMessage().contains("users.UK_6dotkott2kjsp8vw4d0m25fb7")){
                throw new DataIntegrityViolationException("Đã có email này!");
            }else{
                throw new DataIntegrityViolationException("Đã có username này!");
            }
        }
    }

    // @Cacheable()
    public AuthenticationResponse authenticate(LoginRequestDTO request) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername().trim(),
                            request.getPassword().trim()));
        } catch (Exception e) {
            throw new NullPointerException("Sai tài khoản hoặc mật khẩu!");
        }

        User user = (User) authentication.getPrincipal();
        if(StatusEnum.INACTIVE.equals(user.getStatus())){
            throw new AuthenException("Tài khoản này chưa được xác thực");
        }else if(StatusEnum.DELETED.equals(user.getStatus())){
            throw new AuthenException("Tài khoản này đã bị vô hiệu hoá. Vui lòng liên hệ quản trị viên của hệ thống");
        }
        validateUserStatusForLogin(user);
        AuthenticationResponse authenticationResponse = authMapper.toAuthenticationResponse(user);
        String refresh = UUID.randomUUID().toString();
        authenticationResponse.setToken(jwtService.generateToken(user, refresh, false));
        authenticationResponse.setRefreshToken(refresh);
        return authenticationResponse;
    }

    public AuthenticationResponse loginGoogle(LoginGoogleRequest loginGoogleRequest) {

        try {

            // Supabase sử dụng thuật toán HS256
            Algorithm algorithm = Algorithm.HMAC256(supabaseJwtSecret);

            // 2. Xây dựng bộ xác thực (Verifier)
            // Bạn có thể thêm các yêu cầu khác như 'issuer' hoặc 'audience' nếu cần
            JWTVerifier verifier = JWT.require(algorithm)
                    .withAudience("authenticated") // JWT của Supabase thường có aud là 'authenticated'
                    .build();

            // 3. Xác thực token. Nếu không hợp lệ, nó sẽ ném ra JWTVerificationException
            DecodedJWT decodedJWT = verifier.verify(loginGoogleRequest.getToken());


            // 4. Lấy thông tin từ payload. 'sub' chính là User ID (UUID) trong Supabase
            String supabaseUserId = decodedJWT.getSubject();
            String email = decodedJWT.getClaim("email").asString();
            // (MỚI) Lấy tên đầy đủ từ 'user_metadata' do Google cung cấp
            Map<String, Object> userMetadata = decodedJWT.getClaim("user_metadata").asMap();
            String fullNameFromGoogle = null;
            if (userMetadata != null) {
                // Supabase thường đặt tên đầy đủ vào key 'full_name' hoặc 'name'
                if (userMetadata.containsKey("full_name")) {
                    fullNameFromGoogle = (String) userMetadata.get("full_name");
                } else if (userMetadata.containsKey("name")) {
                    fullNameFromGoogle = (String) userMetadata.get("name");
                }
            }

            // Tìm hoặc tạo mới User
            User user = authenRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setFullName(fullNameFromGoogle);
                user.setEmail(email);
                user.setUsername(email);
                user.setRole(RoleEnum.TEACHER); // Hoặc kiểm tra quyền nếu cần
                user.setStatus(StatusEnum.ACTIVE);
                // Tạo workspace nếu là auth mới
//                WorkSpace ws = academicYearService.createWorkspaceForNewUser(user);
//                if (ws != null) {
//                    user.getWorkSpaces().add(ws);
//                    authenRepository.save(user);
//                }
                iWalletService.create(user);
                user = authenRepository.save(user);


            }else {
                if(StatusEnum.DELETED.equals(user.getStatus())){
                    throw new AuthenException("Tài khoản này đã bị vô hiệu hoá. Vui lòng liên hệ quản trị viên của hệ thống");
                }
            }
            validateUserStatusForLogin(user);
            // Sinh token JWT như cũ
            AuthenticationResponse authenticationResponse = authMapper.toAuthenticationResponse(user);
            String refresh = UUID.randomUUID().toString();
            authenticationResponse.setToken(jwtService.generateToken(user, refresh, false));
            authenticationResponse.setRefreshToken(refresh);
            return authenticationResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void validateUserStatusForLogin(User user){
        if(StatusEnum.INACTIVE.equals(user.getStatus())){
            throw new AuthenException("Tài khoản của bạn đã bị dừng hoạt động, vui lòng liên hệ ADMIN");
        }
    }

    public void forgotPasswordRequest(String email) {
        User user = authenRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Không tìm thấy email này!"));
        sendEmailByTemplate(user,templateResetPassword,linkResetPassword);
    }


    @Override
    public void sendEmailByTemplate(User user, String templateId, String link){
        EmailDataRequest emailDataRequest =  new EmailDataRequest();
        emailDataRequest.setToEmail(user.getEmail());
        emailDataRequest.setTemplateId(templateId);

        String token = jwtService.generateToken(user);

        Map<String, String> dynamicData = new HashMap<>();
        dynamicData.put("fullname", user.getFullName());
        dynamicData.put("link", link + "?token=" + token);

        emailDataRequest.setDynamicData(dynamicData);
        String response = emailServiceClient.sendTemplateEmail(emailDataRequest);
    }

    public User resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = accountUtils.getCurrentUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        return authenRepository.save(user);
    }

    public String admin() {
        String name = accountUtils.getCurrentUser().getUsername();
        return name;
    }

    @Override
    public AuthenResponse refresh(RefreshRequest refreshRequest) {
        AuthenResponse authenResponse = new AuthenResponse();
        // String refresh = jwtService.getRefreshClaim(refreshRequest.getToken());
        if (refreshTokenService.validateRefreshToken(refreshRequest.getRefreshToken())) {
            System.out.println(refreshTokenService.getIdFromRefreshToken(refreshRequest.getRefreshToken()));
            User user = authenRepository
                    .findById(refreshTokenService.getIdFromRefreshToken(refreshRequest.getRefreshToken()))
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng!"));
            authenResponse.setToken(jwtService.generateToken(user, refreshRequest.getRefreshToken(), true));
        } else {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        return authenResponse;
    }

    @Override
    public void logout(RefreshRequest refreshRequest) {
        String refresh = refreshRequest.getRefreshToken();
        refreshTokenService.deleteRefreshToken(refresh);
    }

    @Override
    public void verify() {
        User user = accountUtils.getCurrentUser();
        if(StatusEnum.ACTIVE.equals(user.getStatus())){
            throw new BadRequestException("Tài khoàn này đã xác thực rồi");
        }
        user.setStatus(StatusEnum.ACTIVE);
        authenRepository.save(user);
    }

    @Override
    public void resendVerification(String username) {
        User user = authenRepository.findByUsername(username).orElseThrow(() -> new BadRequestException("Tài khoản không tồn tại."));
        if(StatusEnum.ACTIVE.equals(user.getStatus())){
            throw new BadRequestException("Tài khoàn này đã xác thực rồi");
        }
        sendEmailByTemplate(user, templateVerifyUser, linkVerifyUser);
    }
}
