package com.dailycodebuffer.PaymentService.controller;

import com.dailycodebuffer.PaymentService.model.PaymentRequest;
import com.dailycodebuffer.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseEntity<Long> addProduct(@RequestBody PaymentRequest paymentRequest) {
        return new ResponseEntity<Long>(paymentService.doPayment(paymentRequest),HttpStatus.OK);
    }


}
