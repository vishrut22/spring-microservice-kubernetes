package com.dailycodebuffer.PaymentService.service;

import com.dailycodebuffer.PaymentService.entity.TransactionDetails;
import com.dailycodebuffer.PaymentService.model.PaymentMode;
import com.dailycodebuffer.PaymentService.model.PaymentRequest;
import com.dailycodebuffer.PaymentService.model.PaymentResponse;
import com.dailycodebuffer.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details :{}", paymentRequest);
        TransactionDetails transactionDetails = TransactionDetails.builder().paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS").orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber()).amount(paymentRequest.getAmount())
                .build();
        TransactionDetails transactionDetailsSaved = transactionDetailsRepository.save(transactionDetails);
        log.info("Transaction saved with id :{}", transactionDetailsSaved.getId());
        return transactionDetailsSaved.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(long orderId) {
        log.info("Get payment details for order id : {}", orderId);
        TransactionDetails transactionDetailsforOrder = transactionDetailsRepository.findByOrderId(orderId);
        PaymentResponse paymentResponse = PaymentResponse.builder().paymentId(transactionDetailsforOrder.getId())
                .orderId(transactionDetailsforOrder.getOrderId())
                .paymentDate(transactionDetailsforOrder.getPaymentDate()).amount(transactionDetailsforOrder.getAmount())
                .paymentMode(PaymentMode.valueOf(transactionDetailsforOrder.getPaymentMode()))
                .status(transactionDetailsforOrder.getPaymentStatus())
                .build();
        log.info("Payment details for order : {}",paymentResponse);
        return paymentResponse;
    }


}
