package com.marty.entity;

import com.marty.json.input.Payment;
import com.marty.json.input.PaymentPlan;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentEntity {
    Integer paymentPlanId;  // associated payment plan for this payment
    BigDecimal amount;      // amount of payment
    Date date;              // payment date

    public PaymentEntity(Payment payment) {
        this.paymentPlanId = payment.getPaymentPlanId();
        this.amount = payment.getAmount();
        this.date = payment.getDate();
    }

    public Date getDate() {
        return this.date;
    }

    public Integer getPaymentPlanId() {
        return paymentPlanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
