package com.BE.mapper;

import com.BE.model.entity.*;
import com.BE.model.request.*;
import com.BE.model.response.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletResponse toResponse(Wallet wallet);
    WalletTransactionResponse toResponse(WalletTransaction transaction);
}

