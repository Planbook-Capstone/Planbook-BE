package com.BE.service.implementServices;

import com.BE.enums.TransactionType;
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

        User user = authenRepository.findById(userId).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        Wallet wallet = user.getWallet();

        Integer tokenBefore = wallet.getBalance();
        Integer amount = request.getAmount();

        if (wallet.getBalance() < amount) {
            throw new IllegalStateException("Không đủ token trong ví");
        }

        WalletTransaction transaction = WalletTransaction.builder()
                .orderId(UUID.randomUUID()) // hoặc truyền từ request nếu cần
                .tokenBefore(tokenBefore)
                .tokenChange(amount)
                .type(TransactionType.TOOL_USAGE)
                .description(request.getDescription())
                .wallet(wallet)
                .createdAt(dateNowUtils.getCurrentDateTimeHCM())
                .build();

        transaction.addWallet(wallet);

        wallet.setBalance(tokenBefore - amount);
        wallet = walletRepository.save(wallet);

        return walletMapper.toResponse(wallet);
    }

    @Override
    @Transactional
    public WalletResponse refund(WalletTokenRequest request) {
        User user = authenRepository.findById(request.getUserId()).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        Wallet wallet = user.getWallet();

        int tokenBefore = wallet.getBalance();
        int tokenChange = request.getAmount();

        wallet.setBalance(tokenBefore + tokenChange); // ✅ Cộng lại số token đã mất
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .orderId(UUID.randomUUID()) // hoặc truyền từ request nếu cần
                .tokenBefore(tokenBefore)
                .tokenChange(tokenChange)
                .type(TransactionType.REFUND)
                .description(request.getDescription())
                .wallet(wallet)
                .createdAt(dateNowUtils.getCurrentDateTimeHCM())
                .build();
        transaction.addWallet(wallet);

        wallet = walletRepository.save(wallet);

        return walletMapper.toResponse(wallet);
    }




}
