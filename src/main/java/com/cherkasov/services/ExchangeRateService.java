package com.cherkasov.services;


import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import com.cherkasov.repositories.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public void save(final ExchangeRate exchangeRate) {
        exchangeRateRepository.setCurrentStatusFalse(exchangeRate.getSource(),
                exchangeRate.getCurrency());
        exchangeRateRepository.save(exchangeRate);
    }

    public List<ExchangeRate> findCurrentExchangeRates(final String source, final Currency currency) {
        return exchangeRateRepository.findCurrentExchangeRates(source, currency);

    }

    public CurrentAndAvgExchangeRatesDto findAllCurrentAndAvgExchangeRates() {
        return exchangeRateRepository.findAllCurrentAndAvgExchangeRates()
                .orElseGet(this::createDefaultCurrentAndAvgExchangeRatesDto);
    }

    private CurrentAndAvgExchangeRatesDto createDefaultCurrentAndAvgExchangeRatesDto() {
        return new CurrentAndAvgExchangeRatesDto(createDefaultExchangeRates(),createDefaultExchangeRates(),
                createDefaultExchangeRates(),createDefaultExchangeRates(),0.00, 0.00, 0.00, 0.00);
    }

    private ExchangeRate createDefaultExchangeRates() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId("no Data");
        exchangeRate.setSource("no Data");
        exchangeRate.setCurrency(Currency.EUR);
        exchangeRate.setBuyRate(0.00);
        exchangeRate.setSaleRate(0.00);
        return exchangeRate;
    }

    public AvgExchangeRatesForPeriodDto findExchangeRatesForPeriod(final LocalDateTime beginning,
                                                                   final LocalDateTime end) {
        return exchangeRateRepository.findExchangeRatesForPeriod(beginning, end)
                .orElseGet(this::createDefaultAvgExchangeRatesForPeriod);
    }

    private AvgExchangeRatesForPeriodDto createDefaultAvgExchangeRatesForPeriod() {
        return new AvgExchangeRatesForPeriodDto(0.00, 0.00, 0.00, 0.00);
    }
}
