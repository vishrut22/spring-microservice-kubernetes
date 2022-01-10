package com.dailycodebuffer.PaymentService.repository;

import com.dailycodebuffer.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long> {
    public TransactionDetails findByOrderId(long orderId);
}
