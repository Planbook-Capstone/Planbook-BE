package com.BE.controller;

import com.BE.model.request.WalletTokenRequest;
import com.BE.model.request.WalletTransactionFilterRequest;
import com.BE.model.request.WalletTransactionRequest;
import com.BE.model.response.WalletTransactionResponse;
import com.BE.service.interfaceServices.IWalletService;
import com.BE.utils.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class WalletController {

    private final IWalletService walletService;
    private final ResponseHandler responseHandler;

    @GetMapping
    @Operation(summary = "Lấy thông tin ví người dùng (theo userId hoặc tự động từ token)")
    public ResponseEntity<?> getWallet(
            @Parameter(
                    description = "ID người dùng (UUID). Nếu không truyền, hệ thống sẽ lấy từ token.",
                    example = "aa9f1252-382e-4e70-b5d5-6d5b5d20cacc"
            )
            @RequestParam(required = false) UUID userId
    ) {
        return responseHandler.response(200, "Lấy thông tin ví người dùng thành công", walletService.getByUser(userId));
    }

    @PostMapping("/recharge")
    @Operation(summary = "Nạp token vào ví")
    public ResponseEntity recharge(@Valid @RequestBody WalletTransactionRequest request) {
        return responseHandler.response(200,"Nạp token vào ví thành công", walletService.recharge(request));
    }


    @GetMapping("/transactions")
    @Operation(
            summary = "Lấy lịch sử ví",
            description = "Lọc theo userId (hoặc từ token), loại giao dịch, khoảng thời gian, phân trang và sắp xếp."
    )
    public ResponseEntity<?> getTransactions(@Valid @ParameterObject WalletTransactionFilterRequest request) {
        Page<WalletTransactionResponse> result = walletService.getTransactions(request);
        return responseHandler.response(200, "Lấy lịch sử giao dịch ví thành công", result);
    }


    @PostMapping("/deduct")
    @Operation(summary = "Trừ token khỏi ví")
    public ResponseEntity<?> deduct(@Valid @RequestBody WalletTokenRequest request) {
        return responseHandler.response(200, "Trừ token thành công", walletService.deduct(request));
    }

    @PostMapping("/refund")
    @Operation(summary = "Hoàn token vào ví do lỗi khi sử dụng tool")
    public ResponseEntity<?> refund(@Valid @RequestBody WalletTokenRequest request) {
        return responseHandler.response(200, "Hoàn token thành công", walletService.refund(request));
    }


}
