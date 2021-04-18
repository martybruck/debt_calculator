package com.marty.service;

import com.marty.entity.DebtEntity;
import com.marty.entity.PaymentEntity;
import com.marty.entity.PaymentPlanEntity;
import com.marty.json.output.DebtOutput;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class DebtOutputCalculator {

    DebtEntity debtEntity;
    Optional<PaymentPlanEntity> paymentPlanEntity ;

    public DebtOutputCalculator(DebtEntity debtEntity) {
        this.debtEntity = debtEntity;
        this.paymentPlanEntity = debtEntity.getPaymentPlanEntity();

    }
    public DebtOutput generateDebtOutput() {
        DebtOutput debtOutput = new DebtOutput(debtEntity);
        debtOutput.setInPaymentPlan(isInPaymentPlan());
        debtOutput.setNextPaymentDueDate(getNextPaymentDueDate());
        debtOutput.setRemainingAmount(getRemainingAmount());
        return debtOutput;
    }

    public boolean isInPaymentPlan() {
        return paymentPlanEntity
                .map( pp -> getRemainingAmount().compareTo(BigDecimal.ZERO) > 0)
                .orElse(false);
    }

    /**
     * Payment plan is complete if the amount of payments is >= the amount_to_pay
     * @return true if plan is active
     */

    /**
     * Total amount to pay minus payments.
     * Total amount to pay is from paymentPlan if it exists, otherwise it is the debt amount
     * @return
     */
    public BigDecimal getRemainingAmount() {
        // if payment plan exists, return amount to pay from payment plan  minus any payments
        // otherwise return debt amount
        return paymentPlanEntity.map(p->p.getAmountToPay().subtract(getTotalPayments()))
                .orElse(debtEntity.getAmount());
    }

    /**
     * Get the most recent payment date. NOTE: Do not assume that the payments are in order.
     * If no payments have been received, use the start date of the plan.
     * If no payment plan, return null
     * @return - most recent payment date
     */
    private Date getMostRecentPaymentDate() {
        Date paymentDate = null;  // stays null if no payment plan
        if (paymentPlanEntity.isPresent()) {
            // if there are any payments, get the most recent one by date. If no payments, get the
            // start of the payment plan
            List<PaymentEntity> payments = paymentPlanEntity.get().getPaymentEntities();
            paymentDate = CollectionUtils.isNotEmpty(payments) ?
                    Collections.max(payments, Comparator.comparing(PaymentEntity::getDate)).getDate() :
                    getStartDate();
        }
        return paymentDate;
    }

    /**
     * Return the total payments made on this plan. If no plan, return 0
     * @return
     */
    private BigDecimal getTotalPayments() {
        BigDecimal totalPayments = BigDecimal.ZERO;
        if (paymentPlanEntity.isPresent()) {
            // plan exists. Add all payments
            List<PaymentEntity> payments = paymentPlanEntity.get().getPaymentEntities();
            totalPayments =  payments.stream()
                .reduce(BigDecimal.ZERO, (subtotal, payment) -> payment.getAmount().add(subtotal), BigDecimal::add);
        }
        return totalPayments;

    }


    /**
     * Get start of plan or null if no plan
     * @return
     */
    private Date getStartDate() {
        return paymentPlanEntity.map(pp-> pp.getStartDate()).orElse(null);
    }

    /**
     * Return the number of cycle days for a plan or null if no plan
     * @return
     */
    private Integer getPaymentCycleDays() {
        return paymentPlanEntity.map(pp -> pp.getInstallmentFrequency().getPaymentCycleDays()).orElse(null);
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
    public Date getNextPaymentDueDate() {
        Date nextPaymentDate = null;

        if (isInPaymentPlan()) {
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
