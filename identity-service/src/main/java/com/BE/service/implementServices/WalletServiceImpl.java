package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.WalletMapper;
import com.BE.model.entity.User;
import com.BE.model.entity.Wallet;
import com.BE.model.entity.WalletTransaction;
import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.WalletResponse;
import com.BE.model.response.WalletTransactionResponse;
import com.BE.repository.AuthenRepository;
import com.BE.repository.WalletRepository;
import com.BE.repository.WalletTransactionRepository;
import com.BE.service.interfaceServices.IWalletService;
import com.BE.utils.AccountUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements IWalletService {
    private final WalletRepository walletRepository;
    private final AccountUtils accountUtils;
    private final WalletTransactionRepository transactionRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletResponse getByUser() {
        Wallet wallet = accountUtils.getCurrentUser().getWallet();
        return walletMapper.toResponse(wallet);
    }

    @Override
    public List<WalletTransactionResponse> getTransactions() {
        Wallet wallet = accountUtils.getCurrentUser().getWallet();
        return wallet.getTransactions().stream()
                .map(walletMapper::toResponse)
                .toList();
    }

    @Override
    public Wallet create(User user) {
        Wallet wallet = new Wallet();
        wallet.addUser(user);
//        return walletRepository.save(wallet);
        return wallet;
    }

    @Transactional
    @Override
    public WalletTransactionResponse recharge(WalletTransactionRequest request) {
        Wallet wallet = accountUtils.getCurrentUser().getWallet();
        int before = wallet.getBalance();
        int after = before + request.getTokenChange();
        wallet.setBalance(after);

        WalletTransaction txn = WalletTransaction.builder()
                .tokenBefore(before)
                .tokenChange(request.getTokenChange())
                .type(request.getType())
                .description(request.getDescription())
                .orderId(request.getOrderId())
                .build();

        txn.addWallet(wallet);

        walletRepository.save(wallet);
//        transactionRepository.save(txn);

        return walletMapper.toResponse(txn);
    }
}
