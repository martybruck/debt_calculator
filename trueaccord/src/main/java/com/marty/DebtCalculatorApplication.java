package com.marty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marty.entity.DebtEntity;
import com.marty.json.output.DebtOutput;
import com.marty.service.DebtEntityLoader;
import com.marty.service.DebtOutputCalculator;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DebtCalculatorApplication {
    Logger logger = Logger.getLogger("DebtCalculatorApplication");


    public static void main (String...args) {
        new DebtCalculatorApplication().run();
    }

    public void run()  {
        DebtEntityLoader debtEntityLoader = new DebtEntityLoader();


        try {
            List<DebtEntity> debtEntities = debtEntityLoader.loadJson();
            List<DebtOutput> debtOutputs =
            debtEntities.stream().map(de -> new DebtOutputCalculator(de)
                    .generateDebtOutput())
                    .collect(Collectors.toList());
            outputResults(debtOutputs);


        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to process debt json: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Output results into required format of JsonLines. Use ObjectMapper and configure default date format
     * @param results
     */
    private void outputResults(List<DebtOutput> results) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(df);

        results.forEach(r -> {
            try {
                String json = mapper.writeValueAsString(r);
                System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}
