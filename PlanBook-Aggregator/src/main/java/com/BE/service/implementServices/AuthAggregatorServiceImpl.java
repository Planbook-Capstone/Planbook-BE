package com.BE.service.implementServices;

import com.BE.feign.AuthServiceClient;
import com.BE.feign.UserServiceClient;
import com.BE.mapper.AuthAggregatorMapper;
import com.BE.model.request.AuthenticationRequest;
import com.BE.model.request.RegisterAggregatorRequest;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.response.DataResponseDTO;
import com.BE.model.response.UserProfileResponse;
import com.BE.service.interfaceServices.IAuthAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthAggregatorServiceImpl implements IAuthAggregatorService {

    private final AuthServiceClient authClient;
    private final UserServiceClient userClient;
    private final AuthAggregatorMapper mapper;

    @Override
    public AuthenticationResponse register(RegisterAggregatorRequest req) {
        AuthenticationRequest auth = mapper.toAuthenticationRequest(req);

        UserProfileRequest profile = UserProfileRequest.builder()
                .fullName(req.getFullName())
                .build();

        DataResponseDTO<AuthenticationResponse> authRes = authClient.register(auth);

        DataResponseDTO<UserProfileResponse> userProfile = userClient.createProfile(authRes.getData().getId(), profile);

        authRes.getData().setFullName(userProfile.getData().getFullName());
        return authRes.getData();
    }
}
