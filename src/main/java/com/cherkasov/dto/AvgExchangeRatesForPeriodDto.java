package com.cherkasov.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class AvgExchangeRatesForPeriodDto {
    private Double avgUsdBuyExchangeRate;
    private Double avgUsdSaleExchangeRate;
    private Double avgEurBuyExchangeRate;
    private Double avgEurSaleExchangeRate;

}
