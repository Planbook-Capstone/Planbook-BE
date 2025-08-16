package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.WalletMapper;
import com.BE.model.entity.User;
import com.BE.model.entity.Wallet;
import com.BE.model.entity.WalletTransaction;
import com.BE.model.request.WalletTokenRequest;
import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.WalletResponse;
import com.BE.model.response.WalletTransactionResponse;
import com.BE.repository.AuthenRepository;
import com.BE.repository.WalletRepository;
import com.BE.service.interfaceServices.IWalletService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
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
    private final AuthenRepository authenRepository;
    private final WalletMapper walletMapper;
    private final DateNowUtils dateNowUtils;

    @Override
    public WalletResponse getByUser(UUID id) {
        Wallet wallet;
        if (id != null) {
            User user = authenRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
            wallet = user.getWallet();
        }else{
            wallet = accountUtils.getCurrentUser().getWallet();
        }
        return walletMapper.toResponse(wallet);
    }

//    @Override
//    public WalletResponse getByUserId(UUID id) {
//        User user = authenRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
//        Wallet wallet = user.getWallet();
//        return walletMapper.toResponse(wallet);
//    }

    @Override
    public List<WalletTransactionResponse> getTransactions(UUID id) {
        Wallet wallet;
        if (id != null) {
            User user = authenRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
            wallet = user.getWallet();
        } else {
            wallet = accountUtils.getCurrentUser().getWallet();
        }

        return wallet.getTransactions()
                .stream()
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
        User user = authenRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        Wallet wallet = user.getWallet();
        int before = wallet.getBalance();
        int after = before + request.getTokenChange();
        wallet.setBalance(after);

        WalletTransaction txn = WalletTransaction.builder()
                .tokenBefore(before)
                .tokenChange(request.getTokenChange())
                .type(request.getType())
                .description(request.getDescription())
                .orderId(request.getOrderId())
                .createdAt(dateNowUtils.getCurrentDateTimeHCM())
                .build();

        txn.addWallet(wallet);

        walletRepository.save(wallet);
//        transactionRepository.save(txn);

        return walletMapper.toResponse(txn);
    }


    @Override
    @Transactional
    public WalletResponse deduct(WalletTokenRequest request) {
        UUID userId = request.getUserId();
        Integer amount = request.getAmount();

        User user = authenRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        Wallet wallet = user.getWallet();

        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Không đủ token trong ví");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet = walletRepository.save(wallet);

        return walletMapper.toResponse(wallet);
    }

    @Override
    public boolean hasSufficientToken(WalletTokenRequest request) {
        UUID userId = request.getUserId();
        int amount = request.getAmount();

        if (amount <= 0) return true;

        User user = authenRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        Wallet wallet = user.getWallet();
        return wallet.getBalance() >= amount;
    }


}
