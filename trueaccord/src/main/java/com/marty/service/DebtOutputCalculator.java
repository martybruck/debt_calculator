package com.marty.service;

import com.marty.json.input.Debt;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


public class DebtOutputCalculator {
    private Debt debt;
    private Optional<PaymentPlanOutputCalculator> paymentPlanOutputCalculator = Optional.empty();


    public DebtOutputCalculator(Debt debt) {
        this .debt = debt;
    }

    public Integer getId() {
        return debt.getId();
    }

    public BigDecimal getAmount() {
        return debt.getAmount();
    }

    public void setPaymentPlanOutputCalculator(Optional<PaymentPlanOutputCalculator> paymentPlanOutputCalculator) {
        this.paymentPlanOutputCalculator = paymentPlanOutputCalculator;
    }

    public boolean isInPaymentPlan() {
        return paymentPlanOutputCalculator.map(PaymentPlanOutputCalculator::isPlanActive).orElse(false);
    }

    public BigDecimal getRemainingAmount() {
        return paymentPlanOutputCalculator.map(p-> p.getAmountLeftToPay()).orElse(debt.getAmount());
    }


    public Date getNextPaymentDueDate() {
        return paymentPlanOutputCalculator
                .map(PaymentPlanOutputCalculator::getNextPaymentDueDate)
                .orElse(null);

    }

}
