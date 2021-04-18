package com.marty.service;

import com.marty.json.input.Payment;
import com.marty.json.input.PaymentPlan;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PaymentPlanOutputCalculator {
    private PaymentPlan paymentPlan;
    private List<Payment> payments = new ArrayList<>();

    public PaymentPlanOutputCalculator(PaymentPlan paymentPlan) {
        this.paymentPlan = paymentPlan;
    }


    int getId() {
        return paymentPlan.getId();
    }

    void addPayment(Payment payment) {
        this.payments.add(payment);
    }

    /**
     * Payment plan is complete if the amount of payments is >= the amount_to_pay
     * @return true if plan is active
     */
    boolean isPlanActive() {
        return getAmountLeftToPay().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getAmountLeftToPay() {
        return paymentPlan.getAmountToPay().subtract(getTotalPayments());
    }

    /**
     * Get the most recent payment date. NOTE: Do not assume that the payments are in order.
     * If no payments have been received, use the start date of the plan
     * @return - most recenet payment date
     */
    private Date getMostRecentPaymentDate() {
        return (CollectionUtils.isNotEmpty(payments)) ?
            Collections.max(payments, Comparator.comparing(Payment::getDate)).getDate() :
            paymentPlan.getStartDate();

    }

    private BigDecimal getTotalPayments() {
        return payments.stream()
                .reduce(new BigDecimal(0.0),
                        (subtotal, payment) -> payment.getAmount().add(subtotal), BigDecimal::add);
    }


    private Date getStartDate() {
        return paymentPlan.getStartDate();
    }

    private Integer getPaymentCycleDays() {
        return paymentPlan.getInstallmentFrequency().getPaymentCycleDays();
    }

    /**
     * Next payment due date is based on the start date, payment frequency, and last payment date.
     * Note the following:
     * - It is assumed that payment dates are not in the future, but the algorithm should still work
     * - payments may not fall on the actual due date, so the algorithm for calculating the next due date is as follows:
     * -  calculate next payment cycle # as trunc(lastPayDate - startDate) / paymentFreqInDays) + 1)
     *
     * Also note: This requirement assumes that the payment will be based on the last payment date. If the last payment is
     * deliquent, this would put the payer behind schedule. It should really be based on the schedule relative to the current date
     *
     * @return - next payment due date
     */
    Date getNextPaymentDueDate() {
        Date nextPaymentDate = null;

        if (isPlanActive()) {
            // First calculate ho many days from start of debt to most recent payment
            Date lastPayment = getMostRecentPaymentDate();
            Date startOfPlan = getStartDate();
            long startToRecentMs = lastPayment.getTime() - startOfPlan.getTime();
            long startOfDebtToLastPaymentDays = TimeUnit.DAYS.convert(startToRecentMs, TimeUnit.MILLISECONDS);

            // now determine how many cycles have occured (truncating in case payment is late), and add one to get the next cycle date
            long nextPaymentCycleNumber = startOfDebtToLastPaymentDays / getPaymentCycleDays() + 1;

            // finally calculate the next payment date by taking the start date, and adding the next cycle # * days/cycle
            Calendar c = Calendar.getInstance();
            c.setTime(startOfPlan);
            c.add(Calendar.DATE, new Long(nextPaymentCycleNumber * getPaymentCycleDays()).intValue());
            nextPaymentDate = c.getTime();

        }
        return nextPaymentDate;
    }
}
