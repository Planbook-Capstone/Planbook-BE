package com.BE.service.interfaceServices;

import com.BE.model.entity.User;
import com.BE.model.entity.Wallet;
import com.BE.model.request.WalletTokenRequest;
import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.WalletResponse;
import com.BE.model.response.WalletTransactionResponse;

import java.util.List;
import java.util.UUID;

public interface IWalletService {
    WalletResponse getByUser(UUID id);
    WalletTransactionResponse recharge(WalletTransactionRequest request);

    WalletResponse deduct(WalletTokenRequest request);

    List<WalletTransactionResponse> getTransactions(UUID id);

    Wallet create(User user);

    boolean hasSufficientToken(WalletTokenRequest request);


}

