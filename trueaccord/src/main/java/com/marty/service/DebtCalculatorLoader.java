package com.marty.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.marty.json.input.Debt;
import com.marty.json.input.Payment;
import com.marty.json.input.PaymentPlan;
import com.marty.json.output.DebtOutput;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to load and store all incoming json. I originally thought about makind a separate class for each one, but
 * this class was so simple that I just combined everyting into one class. I also thought about abstracting this a bit
 * more but decided not to for the same reason.
 */
public class DebtCalculatorLoader {

    private final static String debtsUrl= "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/debts";
    private final static String paymentPlansUrl= "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payment_plans";
    private final static String paymentsUrl = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payments";

    private List<Debt> debts;
    private List<PaymentPlan> paymentPlans;
    private List<Payment> payments;
    private ObjectMapper objectMapper;

    // indexes to optimize lookups. Lookups will be O(1)

    private Map<Integer, DebtOutputCalculator> debtById = new HashMap<>();
    private Map<Integer, PaymentPlanOutputCalculator> planById = new HashMap<>();

    /**
     * Load all json-based information into

     * @return nothing
     * @throws IOException
     */

    public DebtCalculatorLoader() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(df);

    }

    public void loadJson() throws IOException {
        debts = Arrays.asList(objectMapper.readValue(new URL(debtsUrl), Debt[].class));
        paymentPlans = Arrays.asList(objectMapper.readValue(new URL(paymentPlansUrl), PaymentPlan[].class));
        payments = Arrays.asList(objectMapper.readValue(new URL(paymentsUrl), Payment[].class));
        loadDebts(debts);
        loadPaymentPlans(paymentPlans);
        loadPayments(payments);
    }



    private void loadDebts(List<Debt> debts) {
        // TODO: Ensure no multiple ids
        if (CollectionUtils.isNotEmpty(debts)) {
            debts.forEach(d -> {
                DebtOutputCalculator debtOutputCalculator = new DebtOutputCalculator(d);
                debtById.put(debtOutputCalculator.getId(), debtOutputCalculator);
            });
        }
    }


    /**
     * Load all of the payment plans. To do this, find the debt associated with this payment plan
     * by debt id, then associate the payment plan with it. NOTE that this assumes that no payment plan
     * is passed with an invalid debt id
     * TODO: validate debtID exists
     * @param paymentPlans
     */
    private void loadPaymentPlans(List<PaymentPlan> paymentPlans) {
        if (CollectionUtils.isNotEmpty(paymentPlans)) {
            paymentPlans.forEach(pp -> {
                DebtOutputCalculator foundDebt = debtById.get(pp.getDebtId());
                PaymentPlanOutputCalculator paymentPlanOutputCalculator = new PaymentPlanOutputCalculator(pp);
                foundDebt.setPaymentPlanOutputCalculator(Optional.ofNullable(paymentPlanOutputCalculator));
                planById.put(paymentPlanOutputCalculator.getId(), paymentPlanOutputCalculator);
            });
        }
    }

    /**
     * Load all payments. To do this, find the payment plan associated with the payment by id
     * and add it to that payment plan (calculator). NOTE: this assumes that no payments are passed
     * with invalid payment plan ids
     * TODO: validate payment plan exists
     * @param payments
     */
    private void loadPayments(List<Payment> payments) {
        if (CollectionUtils.isNotEmpty(payments)) {
            payments.forEach(p -> {
                PaymentPlanOutputCalculator foundPlan = planById.get(p.getPaymentPlanId());
                foundPlan.addPayment(p);
            });
        }

    }

    /**
     * Method to map the results to the final Json objects. NOTE: Even though the DebtOutputCalculator
     * could have been used to directly generate the JSON, it is a better separation of concerns
     * to have the output contained in a separate JSON object, which can be either printed (as in this example),
     * or serialized and sent via http to another destination.
     *
     * Also order is not assumed, so the output is sorted by id before returning.
     * @return
     */
    public List<DebtOutput> getDebtOutputs() {
        return debtById.values().stream()
                .map(DebtOutput::new)
                .sorted(Comparator.comparing(DebtOutput::getId))
                .collect(Collectors.toList());
    }

}
