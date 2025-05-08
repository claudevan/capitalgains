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
                // Recalcula o preço médio ponderado
                BigDecimal totalCost = weightedAverageCost.multiply(BigDecimal.valueOf(totalQuantity))
                        .add(op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity())));
                totalQuantity += op.getQuantity();
                weightedAverageCost = totalCost.divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP);
                results.add(new TaxResult(BigDecimal.ZERO));
            } else if (op.getOperation().equals("sell")) {
                // Calcula o valor total da operação de venda
                BigDecimal totalSellValue = op.getUnitCost().multiply(BigDecimal.valueOf(op.getQuantity()));

                // Calcula o custo total baseado no preço médio ponderado
                BigDecimal totalCost = weightedAverageCost.multiply(BigDecimal.valueOf(op.getQuantity()));
                BigDecimal profit = totalSellValue.subtract(totalCost); // Lucro ou prejuízo da operação

                if (profit.compareTo(BigDecimal.ZERO) > 0) {
                    // Caso de lucro: deduz o prejuízo acumulado antes de calcular o imposto
                    if (accumulatedLoss.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal adjustedProfit = profit.subtract(accumulatedLoss);

                        if (adjustedProfit.compareTo(BigDecimal.ZERO) > 0) {
                            accumulatedLoss = BigDecimal.ZERO; // Prejuízo completamente deduzido
                            BigDecimal tax = adjustedProfit.multiply(BigDecimal.valueOf(0.2));
                            results.add(new TaxResult(tax));
                        } else {
                            accumulatedLoss = accumulatedLoss.subtract(profit);
                            results.add(new TaxResult(BigDecimal.ZERO));
                        }
                    } else {
                        // Sem prejuízo acumulado, calcula imposto normalmente

                        // Regras para isenção de imposto (valor total <= R$ 20.000,00)
                        if (totalSellValue.compareTo(BigDecimal.valueOf(20000)) <= 0) {
                            results.add(new TaxResult(BigDecimal.ZERO));
                           continue;
                        }

                        BigDecimal tax = profit.multiply(BigDecimal.valueOf(0.2));
                        results.add(new TaxResult(tax));
                    }
                } else {
                    // Caso de prejuízo: acumula o prejuízo
                    accumulatedLoss = accumulatedLoss.add(profit.abs());
                    results.add(new TaxResult(BigDecimal.ZERO));
                }

                // Atualiza a quantidade total de ações
                totalQuantity -= op.getQuantity();
            }
        }

        return results;
    }
}
