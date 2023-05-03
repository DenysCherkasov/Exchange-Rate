package com.cherkasov.repositories;

import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomizedExchangeRateRepository {
    List<ExchangeRate> findCurrentExchangeRates(final String source, final Currency currency);

    @Transactional
    void setCurrentStatusFalse(final String source, final Currency currency);

    Optional<CurrentAndAvgExchangeRatesDto> findAllCurrentAndAvgExchangeRates();

    Optional<AvgExchangeRatesForPeriodDto> findExchangeRatesForPeriod(final LocalDateTime beginning,
                                                                      final LocalDateTime end);

}
