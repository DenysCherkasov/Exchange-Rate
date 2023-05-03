package com.cherkasov.dto;

import com.cherkasov.models.ExchangeRate;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CurrentAndAvgExchangeRatesDto {
    private ExchangeRate PrivatUsdExchangeRate;
    private ExchangeRate PrivatEurExchangeRate;
    private ExchangeRate MonoUsdExchangeRate;
    private ExchangeRate MonoEurExchangeRate;
    private Double avgUsdBuyExchangeRate;
    private Double avgUsdSaleExchangeRate;
    private Double avgEurBuyExchangeRate;
    private Double avgEurSaleExchangeRate;

}
