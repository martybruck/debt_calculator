package com.marty.entity;

import com.marty.json.input.Debt;

import java.math.BigDecimal;
import java.util.Optional;

public class DebtEntity {

    private Integer id;  // id of this debt
    private BigDecimal amount;  // debt owed to this collector
    private Optional<PaymentPlanEntity> paymentPlanEntity;


    public DebtEntity(Debt debt) {
        this.id = debt.getId();
        this.amount = debt.getAmount();
        this.paymentPlanEntity = Optional.empty();
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Optional<PaymentPlanEntity> getPaymentPlanEntity() {
        return this.paymentPlanEntity;
    }

    public void setPaymentPlanEntity(Optional<PaymentPlanEntity> paymentPlanEntity) {
        this.paymentPlanEntity = paymentPlanEntity;
    }

}
