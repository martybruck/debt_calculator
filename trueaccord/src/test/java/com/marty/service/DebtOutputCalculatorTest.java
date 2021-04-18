package com.marty.service;

import com.marty.entity.DebtEntity;
import com.marty.entity.PaymentEntity;
import com.marty.entity.PaymentPlanEntity;
import com.marty.json.input.Debt;
import com.marty.json.input.InstallmentFrequency;
import com.marty.json.input.Payment;
import com.marty.json.input.PaymentPlan;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


public class DebtOutputCalculatorTest {
    Date planStartDate;
    Date payment1Date;
    Date payment2Date;
    Date expectedNextDueDate;



    @Before
    public void init() {
        planStartDate = new Calendar.Builder().setDate(2020,Calendar.JUNE,1).build().getTime();
        payment1Date = new Calendar.Builder().setDate(2020,Calendar.JUNE,15).build().getTime();
        payment2Date = new Calendar.Builder().setDate(2020,Calendar.JULY,4).build().getTime();
        expectedNextDueDate = new Calendar.Builder().setDate(2020,Calendar.JULY,13).build().getTime();

    }

    @Test
    /**
     * This test verifes that if no payment plan is added to the DebtCalculator, that values are returned per requirements
     */
    public void testInPlanWithNoPlan() {
        BigDecimal paymentAmount = new BigDecimal(2000.0);
        DebtEntity debtEntity = new DebtEntity(new Debt(1, paymentAmount));
        DebtOutputCalculator calculator = new DebtOutputCalculator(debtEntity);
        assertFalse(calculator.isInPaymentPlan());
        assertEquals(paymentAmount, calculator.getRemainingAmount());
        assertNull(calculator.getNextPaymentDueDate());
    }

    @Test
    public void testIsInPaymentPlanNotPaidOff() {
        BigDecimal paymentAmount = new BigDecimal(2200.0);
        DebtEntity debtEntity = new DebtEntity(new Debt(1, paymentAmount));
        PaymentPlanEntity paymentPlan = new PaymentPlanEntity(new PaymentPlan(1, 1, new BigDecimal(2000.0),
                InstallmentFrequency.BI_WEEKLY,new BigDecimal(201.0), planStartDate));
        PaymentEntity payment1 = new PaymentEntity(new Payment(1, new BigDecimal(200.0), payment1Date ));
        PaymentEntity payment2 = new PaymentEntity(new Payment(1, new BigDecimal(200.0), payment2Date));
        paymentPlan.addPaymentEntity(payment1);
        paymentPlan.addPaymentEntity(payment2);
        debtEntity.setPaymentPlanEntity(Optional.ofNullable(paymentPlan));

        DebtOutputCalculator calculator = new DebtOutputCalculator(debtEntity);

        assertTrue(calculator.isInPaymentPlan());
        assertEquals(new BigDecimal(1600.0), calculator.getRemainingAmount());
        assertEquals(expectedNextDueDate, calculator.getNextPaymentDueDate());
    }

    @Test
    public void testIsNotInPaymentPlanPaidOff() {
        DebtEntity debtEntity = new DebtEntity(new Debt(1, new BigDecimal(2200.0)));
        PaymentPlanEntity paymentPlan = new PaymentPlanEntity(new PaymentPlan(1, 1, new BigDecimal(2000.0),
                InstallmentFrequency.BI_WEEKLY,new BigDecimal(201.0), planStartDate));
        PaymentEntity payment1 = new PaymentEntity(new Payment(1, new BigDecimal(200.0), payment1Date ));
        PaymentEntity payment2 = new PaymentEntity(new Payment(1, new BigDecimal(1800.0), payment2Date));
        paymentPlan.addPaymentEntity(payment1);
        paymentPlan.addPaymentEntity(payment2);
        debtEntity.setPaymentPlanEntity(Optional.ofNullable(paymentPlan));
        DebtOutputCalculator calculator = new DebtOutputCalculator(debtEntity);

        assertFalse(calculator.isInPaymentPlan());
        assertNull(calculator.getNextPaymentDueDate());
        assertEquals(BigDecimal.ZERO, calculator.getRemainingAmount());
    }

    @Test
    public void testInPlanNoPaymentsMade() {
        DebtEntity debtEntity = new DebtEntity(new Debt(1, new BigDecimal(2200.0)));
        PaymentPlanEntity paymentPlan = new PaymentPlanEntity(new PaymentPlan(1, 1, new BigDecimal(2000.0),
                InstallmentFrequency.BI_WEEKLY,new BigDecimal(201.0), planStartDate));
        debtEntity.setPaymentPlanEntity(Optional.ofNullable(paymentPlan));
        DebtOutputCalculator calculator = new DebtOutputCalculator(debtEntity);
        assertTrue(calculator.isInPaymentPlan());
        assertEquals(payment1Date, calculator.getNextPaymentDueDate());
        assertEquals(new BigDecimal(2000.0), calculator.getRemainingAmount());

    }

}
