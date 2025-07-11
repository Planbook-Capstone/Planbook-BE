package com.BE.service.interfaceServices;

import com.BE.model.request.RegisterAggregatorRequest;
import com.BE.model.response.AuthenticationResponse;

public interface IAggregatorService {
    AuthenticationResponse register(RegisterAggregatorRequest req);

}
