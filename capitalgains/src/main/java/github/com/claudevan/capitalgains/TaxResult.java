package github.com.claudevan.capitalgains;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TaxResult {

    // Getter
    private BigDecimal tax;

    public TaxResult(BigDecimal tax) {
        this.tax = tax.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
