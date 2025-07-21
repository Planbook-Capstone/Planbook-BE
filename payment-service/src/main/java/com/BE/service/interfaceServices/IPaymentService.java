package com.BE.service.interfaceServices;

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
    PaymentLinkResponseDTO createPaymentLink(CreatePaymentRequestDTO request);
    PaymentLinkResponseDTO retryPayment(RetryPaymentRequestDTO request);
    ObjectNode handlePayosWebhook(ObjectNode body) throws JsonProcessingException;

    List<PaymentTransactionResponse> getTransactionsByOrderId(UUID orderId);

    CancelPaymentResponseDTO cancelAllPendingTransactions(CancelPaymentRequestDTO request);




}
