package com.BE.service.implementServices;

import com.BE.enums.RoleEnum;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.InvalidRefreshTokenException;
import com.BE.mapper.UserMapper;
import com.BE.model.EmailDetail;
import com.BE.model.request.*;
import com.BE.model.response.AuthenResponse;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.entity.User;
import com.BE.model.entity.WorkSpace;
import com.BE.repository.UserRepository;
import com.BE.service.EmailService;
import com.BE.service.JWTService;
import com.BE.service.RefreshTokenService;
import com.BE.service.interfaceServices.IAcademicYearService;
import com.BE.service.interfaceServices.IAuthenticationService;
import com.BE.service.interfaceServices.IWorkSpaceService;
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

import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationImpl implements IAuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTService jwtService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    IAcademicYearService academicYearService;

    @Autowired
    IWorkSpaceService workSpaceService;

    @Value("${supabase.jwt.secret}")
    private String supabaseJwtSecret;


    public User register(AuthenticationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleEnum.TEACHER);
        try {
            // Create workspace for new user
            WorkSpace ws = academicYearService.createWorkspaceForNewUser(user);
            if (ws != null) {
                user.getWorkSpaces().add(ws);
                userRepository.save(user);
                workSpaceService.save(ws);
            }
            return user;
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            throw new DataIntegrityViolationException("Đã có username này!");
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
            throw new NullPointerException("Sai ID hoặc mật khẩu!");
        }

        User user = (User) authentication.getPrincipal();
        AuthenticationResponse authenticationResponse = userMapper.toAuthenticationResponse(user);
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
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = new User();
                user.setFullName(fullNameFromGoogle);
                user.setEmail(email);
                user.setUsername(email);
                user.setRole(RoleEnum.TEACHER); // Hoặc kiểm tra quyền nếu cần

                // Tạo workspace nếu là user mới
                WorkSpace ws = academicYearService.createWorkspaceForNewUser(user);
                if (ws != null) {
                    user.getWorkSpaces().add(ws);
                    userRepository.save(user);
                }

                user = userRepository.save(user);


            }

            // Sinh token JWT như cũ
            AuthenticationResponse authenticationResponse = userMapper.toAuthenticationResponse(user);
            String refresh = UUID.randomUUID().toString();
            authenticationResponse.setToken(jwtService.generateToken(user, refresh, false));
            authenticationResponse.setRefreshToken(refresh);
            return authenticationResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void forgotPasswordRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Không tìm thấy email này!"));

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(user.getEmail());
        emailDetail.setSubject("Khôi phục mật khẩu cho: " + user.getEmail() + "!");
        emailDetail.setMsgBody("aaa");
        emailDetail.setButtonValue("Reset Password");
        emailDetail.setFullName(user.getFullName());
        emailDetail.setLink("http://localhost:5173?token=" + jwtService.generateToken(user));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                emailService.sendMailTemplate(emailDetail);
            }

        };
        new Thread(r).start();

    }

    public User resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = accountUtils.getCurrentUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        return userRepository.save(user);
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
            User user = userRepository
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
}
