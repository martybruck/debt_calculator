package com.marty.json.input;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

/**
 * Debt class represents money owed to a collector
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Debt {

    private Integer id;  // id of this debt
    private BigDecimal amount;  // debt owed to this collector

    public Debt(){}

    public Debt(Integer id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
