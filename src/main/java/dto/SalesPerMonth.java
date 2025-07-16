package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesPerMonth {
    private Integer year;
    private Integer month;
    private Double totalSales;
}
