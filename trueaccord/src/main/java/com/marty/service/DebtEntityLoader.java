package com.marty.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.marty.entity.DebtEntity;
import com.marty.entity.PaymentEntity;
import com.marty.entity.PaymentPlanEntity;
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
public class DebtEntityLoader {

    private final static String debtsUrl= "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/debts";
    private final static String paymentPlansUrl= "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payment_plans";
    private final static String paymentsUrl = "https://my-json-server.typicode.com/druska/trueaccord-mock-payments-api/payments";

    private List<Debt> debts;
    private List<PaymentPlan> paymentPlans;
    private List<Payment> payments;
    private ObjectMapper objectMapper;

    // indexes to optimize lookups. Lookups will be O(1)

    private Map<Integer, DebtEntity> debtById = new HashMap<>();
    private Map<Integer, PaymentPlanEntity> planById = new HashMap<>();

    /**
     * Load all json-based information into

     * @return nothing
     * @throws IOException
     */

    public DebtEntityLoader() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getDefault());
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(df);

    }

    /**
     * Main method that does the following:
     * 1. load debts from rest call and create DebtEntities
     * 2. load payment plans from rest call and add PaymentPlanEntities to DebtEntities
     * 3. load payments from rest call and add Payments to PaymentPlanEntities
     * 4. Return the debt entites sorted by id
     * @return
     * @throws IOException
     */
    public List<DebtEntity> loadJson() throws IOException {
        debts = Arrays.asList(objectMapper.readValue(new URL(debtsUrl), Debt[].class));
        paymentPlans = Arrays.asList(objectMapper.readValue(new URL(paymentPlansUrl), PaymentPlan[].class));
        payments = Arrays.asList(objectMapper.readValue(new URL(paymentsUrl), Payment[].class));
        loadDebts(debts);
        loadPaymentPlans(paymentPlans);
        loadPayments(payments);
        return debtById.values().stream().sorted(Comparator.comparingInt(DebtEntity::getId)).collect(Collectors.toList());
    }



    private void loadDebts(List<Debt> debts) {
        // TODO: Ensure no multiple ids
        if (CollectionUtils.isNotEmpty(debts)) {
            debts.forEach(d -> {
                DebtEntity debtEntity = new DebtEntity(d);
                debtById.put(debtEntity.getId(), debtEntity);
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
                DebtEntity foundDebt = debtById.get(pp.getDebtId());
                PaymentPlanEntity paymentPlanEntity = new PaymentPlanEntity(pp);
                foundDebt.setPaymentPlanEntity(Optional.ofNullable(paymentPlanEntity));
                planById.put(paymentPlanEntity.getId(), paymentPlanEntity);
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
                PaymentPlanEntity foundPlan = planById.get(p.getPaymentPlanId());
                foundPlan.addPaymentEntity(new PaymentEntity(p));
            });
        }

    }

}
