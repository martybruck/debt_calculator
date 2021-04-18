package com.marty.json.input;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents an individual payment made on a payment plan
 * Payment <n--1>PaymentPlan <1--1> Debt cardinality relationship
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Payment {

    Integer paymentPlanId;  // associated payment plan for this payment
    BigDecimal amount;      // amount of payment
    Date date;              // payment date

    public Payment() {}

    public Payment(Integer paymentPlanId, BigDecimal amount, Date date) {
        this.paymentPlanId = paymentPlanId;
        this.amount = amount;
        this.date = date;
    }

    public int getPaymentPlanId() {
        return paymentPlanId;
    }

    public void setPaymentPlanId(int paymentPlanId) {
        this.paymentPlanId = paymentPlanId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}


