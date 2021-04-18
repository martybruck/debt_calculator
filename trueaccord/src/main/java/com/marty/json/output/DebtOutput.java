package com.marty.json.output;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.marty.entity.DebtEntity;
import com.marty.json.serializer.CurrencySerializer;
import com.marty.service.DebtOutputCalculator;

import java.math.BigDecimal;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * This is the class that is used to create the json output. It includes proper serializers as needed.
 */
public class DebtOutput {
    private Integer id;

    @JsonSerialize(using = CurrencySerializer.class)
    private BigDecimal amount;  // debt owed to this collector

    private boolean isInPaymentPlan;    // true if in payment plan

    @JsonSerialize(using = CurrencySerializer.class)
    private BigDecimal remainingAmount; // remaining amount of debt


    private Date nextPaymentDueDate;

    public DebtOutput(DebtEntity debtEntity) {
        this.id = debtEntity.getId();
        this.amount = debtEntity.getAmount();
    }

    public void setInPaymentPlan(boolean inPaymentPlan) {
        isInPaymentPlan = inPaymentPlan;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public void setNextPaymentDueDate(Date nextPaymentDueDate) {
        this.nextPaymentDueDate = nextPaymentDueDate;
    }
}
