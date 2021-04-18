package com.marty.service;

import com.marty.json.input.InstallmentFrequency;
import com.marty.json.input.Payment;
import com.marty.json.input.PaymentPlan;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class PaymentPlanOutputCalculatorTest {

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
    public void testIsInPaymentPlanNotPaidOff() {
        PaymentPlan paymentPlan = new PaymentPlan(1, 1, new BigDecimal(2000.0),
                InstallmentFrequency.BI_WEEKLY,new BigDecimal(201.0), planStartDate);
        Payment payment1 = new Payment(1, new BigDecimal(200.0), payment1Date );
        Payment payment2 = new Payment(1, new BigDecimal(200.0), payment2Date);
        PaymentPlanOutputCalculator calculator = new PaymentPlanOutputCalculator(paymentPlan);
        calculator.addPayment(payment1);
        calculator.addPayment(payment2);
        assertTrue(calculator.isPlanActive());
        assertEquals(new BigDecimal(1600.0), calculator.getAmountLeftToPay());
        assertEquals(expectedNextDueDate, calculator.getNextPaymentDueDate());
    }

    @Test
    public void testIsNotInPaymentPlanPaidOff() {
        PaymentPlan paymentPlan = new PaymentPlan(1, 1, new BigDecimal(2000.0),
                InstallmentFrequency.BI_WEEKLY,new BigDecimal(201.0), planStartDate);
        Payment payment1 = new Payment(1, new BigDecimal(200.0), payment1Date );
        Payment payment2 = new Payment(1, new BigDecimal(1800.0), payment2Date);
        PaymentPlanOutputCalculator calculator = new PaymentPlanOutputCalculator(paymentPlan);
        calculator.addPayment(payment1);
        calculator.addPayment(payment2);
        assertFalse(calculator.isPlanActive());
        assertNull(calculator.getNextPaymentDueDate());
        assertEquals(BigDecimal.ZERO, calculator.getAmountLeftToPay());
    }

    @Test
    public void testInPlanNoPaymentsMade() {
        PaymentPlan paymentPlan = new PaymentPlan(1, 1, new BigDecimal(2000.0),
                InstallmentFrequency.BI_WEEKLY,new BigDecimal(201.0), planStartDate);
        PaymentPlanOutputCalculator calculator = new PaymentPlanOutputCalculator(paymentPlan);
        assertTrue(calculator.isPlanActive());
        assertEquals(payment1Date, calculator.getNextPaymentDueDate());
        assertEquals(new BigDecimal(2000.0), calculator.getAmountLeftToPay());

    }

}
