package com.marty.service;

import com.marty.json.input.Debt;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Test for DebtOutputCalculator. Note that most features are tested under
 * @see PaymentPlanOutputCalculatorTest
 *
 */
public class DebtOutputCalculatorTest {
    @Test
    /**
     * This test verifes that if no payment plan is added to the DebtCalculator, that values are returned per requirements
     */
    public void testInPlanWithNoPlan() {
        BigDecimal paymentAmount = new BigDecimal(2000.0);
        Debt debt = new Debt(1, paymentAmount);
        DebtOutputCalculator calculator = new DebtOutputCalculator(debt);
        assertFalse(calculator.isInPaymentPlan());
        assertEquals(paymentAmount, calculator.getRemainingAmount());
        assertNull(calculator.getNextPaymentDueDate());
    }
}
