package com.marty.json.input;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Payment plan indicates the amount need to resolve a debt as well as payment frequency.
 * Payment <0..1---1> Debt cardinality relationship
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentPlan {
    private Integer id;                             // internal id
    private Integer debtId;                         // id of associated debt
    private BigDecimal amountToPay;                 // total amount paid to resolve payment plan
    private InstallmentFrequency installmentFrequency;  // frequency of payments
    private BigDecimal installmentAmount;           // amount of each payment
    private Date startDate;                     // date when first payment is due

    public PaymentPlan() {}


    public PaymentPlan(Integer id, Integer debtId, BigDecimal amountToPay, InstallmentFrequency installmentFrequency, BigDecimal installmentAmount, Date startDate) {
        this.id = id;
        this.debtId = debtId;
        this.amountToPay = amountToPay;
        this.installmentFrequency = installmentFrequency;
        this.installmentAmount = installmentAmount;
        this.startDate = startDate;
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

}
