package github.com.claudevan.capitalgains;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class CapitalGainsCommands {

    private final ObjectMapper objectMapper;

    public CapitalGainsCommands(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ShellMethod(value = "Calcula os impostos sobre ganho de capital dado um JSON de operações.", key = "calcular")
    public String calcular(String inputJson) {
        try {
            List<Operation> operations = objectMapper.readValue(inputJson, new TypeReference<>() {});
            List<TaxResult> results = calculateTaxes(operations);
            return objectMapper.writeValueAsString(results);
        } catch (Exception e) {
            return "Erro ao processar o JSON: " + e.getMessage();
        }
    }

    List<TaxResult> calculateTaxes(List<Operation> operations) {
        List<TaxResult> results = new ArrayList<>();

        BigDecimal weightedAverageCost = BigDecimal.ZERO;
        int totalQuantity = 0;
        BigDecimal accumulatedLoss = BigDecimal.ZERO;

        for (Operation op : operations) {
            if (op.getOperation().equals("buy")) {
                BigDecimal totalCost = weightedAverageCost.multiply(BigDecimal.valueOf(totalQuantity))
                        .add(op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity())));
                totalQuantity += op.getQuantity();
                weightedAverageCost = totalCost.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
                results.add(new TaxResult(BigDecimal.ZERO));

            } else if (op.getOperation().equals("sell")) {
                BigDecimal totalSellValue = op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity()));

                if (totalSellValue.compareTo(BigDecimal.valueOf(20000)) <= 0) {
                    results.add(new TaxResult(BigDecimal.ZERO));
                    continue;
                }

                BigDecimal totalCost = weightedAverageCost.multiply(BigDecimal.valueOf(op.getQuantity()));
                BigDecimal profit = totalSellValue.subtract(totalCost);

                if (profit.compareTo(BigDecimal.ZERO) > 0) {
                    if (accumulatedLoss.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal adjustedProfit = profit.subtract(accumulatedLoss);
                        if (adjustedProfit.compareTo(BigDecimal.ZERO) > 0) {
                            accumulatedLoss = BigDecimal.ZERO;
                            BigDecimal tax = adjustedProfit.multiply(BigDecimal.valueOf(0.2));
                            results.add(new TaxResult(tax));
                        } else {
                            accumulatedLoss = accumulatedLoss.subtract(profit);
                            results.add(new TaxResult(BigDecimal.ZERO));
                        }
                    } else {
                        BigDecimal tax = profit.multiply(BigDecimal.valueOf(0.2));
                        results.add(new TaxResult(tax));
                    }
                } else {
                    accumulatedLoss = accumulatedLoss.add(profit.abs());
                    results.add(new TaxResult(BigDecimal.ZERO));
                }

                totalQuantity -= op.getQuantity();
            }
        }

        return results;
    }
}
