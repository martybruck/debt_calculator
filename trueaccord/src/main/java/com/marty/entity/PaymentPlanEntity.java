package com.marty.entity;

import com.marty.json.input.InstallmentFrequency;
import com.marty.json.input.PaymentPlan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentPlanEntity {

    private Integer id;                             // internal id
    private Integer debtId;                         // id of associated debt
    private BigDecimal amountToPay;                 // total amount paid to resolve payment plan
    private InstallmentFrequency installmentFrequency;  // frequency of paymentEntities
    private BigDecimal installmentAmount;           // amount of each payment
    private Date startDate;                     // date when first payment is due
    List<PaymentEntity> paymentEntities;

    public PaymentPlanEntity(PaymentPlan paymentPlan) {
        this.id = paymentPlan.getId();
        this.debtId = paymentPlan.getDebtId();
        this.amountToPay = paymentPlan.getAmountToPay();
        this.installmentFrequency = paymentPlan.getInstallmentFrequency();
        this.installmentAmount = paymentPlan.getInstallmentAmount();
        this.startDate = paymentPlan.getStartDate();
        paymentEntities = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }


    public Integer getDebtId() {
        return debtId;
    }


    public BigDecimal getAmountToPay() {
        return amountToPay;
    }


    public InstallmentFrequency getInstallmentFrequency() {
        return installmentFrequency;
    }


    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void addPaymentEntity(PaymentEntity payment) {
        paymentEntities.add(payment);
    }
    public List<PaymentEntity> getPaymentEntities() {
        return this.paymentEntities;
    }

}
