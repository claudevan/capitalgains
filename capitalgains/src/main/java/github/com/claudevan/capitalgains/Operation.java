package github.com.claudevan.capitalgains;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Operation {
    private String operation;
    private int quantity;
    private BigDecimal unitCost;
}
