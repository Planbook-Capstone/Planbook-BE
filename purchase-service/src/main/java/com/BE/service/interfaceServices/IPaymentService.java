package com.BE.service.interfaceServices;

import com.BE.model.entity.PaymentTransaction;
import com.BE.model.request.CancelPaymentRequestDTO;
import com.BE.model.request.CreatePaymentRequestDTO;
import com.BE.model.request.RetryPaymentRequestDTO;
import com.BE.model.response.CancelPaymentResponseDTO;
import com.BE.model.response.PaymentLinkResponseDTO;
import com.BE.model.response.PaymentTransactionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import vn.payos.type.Webhook;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IPaymentService {
    PaymentTransaction createPaymentLink(CreatePaymentRequestDTO request);
    PaymentTransaction retryPayment(RetryPaymentRequestDTO request);
    PaymentTransaction handlePayosWebhook(ObjectNode body) throws Exception;

    List<PaymentTransactionResponse> getTransactionsByOrderId(UUID orderId);

    CancelPaymentResponseDTO cancelAllPendingTransactions(CancelPaymentRequestDTO request);




}
