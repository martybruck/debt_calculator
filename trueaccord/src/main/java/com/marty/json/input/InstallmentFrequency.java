package com.marty.json.input;

public enum InstallmentFrequency {
    WEEKLY(7), BI_WEEKLY(14);

    private final Integer paymentCycleDays;

    InstallmentFrequency(Integer paymentCycleDays) {
        this.paymentCycleDays = paymentCycleDays;
    }

    public Integer getPaymentCycleDays() {
        return paymentCycleDays;
    }
}
