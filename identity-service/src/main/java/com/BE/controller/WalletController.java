package com.BE.controller;

import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.*;
import com.BE.service.interfaceServices.IWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class WalletController {

    private final IWalletService walletService;

    @GetMapping
    @Operation(summary = "Lấy thông tin ví người dùng")
    public ResponseEntity<WalletResponse> getWallet() {
        return ResponseEntity.ok(walletService.getByUser());
    }

    @PostMapping("/recharge")
    @Operation(summary = "Nạp token vào ví")
    public ResponseEntity<WalletTransactionResponse> recharge(@Valid @RequestBody WalletTransactionRequest request) {
        return ResponseEntity.ok(walletService.recharge(request));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Lịch sử nạp ví")
    public ResponseEntity<List<WalletTransactionResponse>> getTransactions() {
        return ResponseEntity.ok(walletService.getTransactions());
    }
}
