package com.BE.service.interfaceServices;

import com.BE.model.entity.User;
import com.BE.model.entity.Wallet;
import com.BE.model.entity.WalletTransaction;
import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.WalletResponse;
import com.BE.model.response.WalletTransactionResponse;

import java.util.List;
import java.util.UUID;

public interface IWalletService {
    WalletResponse getByUser();
    WalletTransactionResponse recharge(WalletTransactionRequest request);
    List<WalletTransactionResponse> getTransactions();

    Wallet create(User user);
}

