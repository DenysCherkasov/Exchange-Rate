package com.cherkasov.services;

import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import com.cherkasov.repositories.ExchangeRateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService target;

    @Test
    public void saveTest() {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setSource("source");
        exchangeRate.setCurrency(Currency.USD);

        target.save(exchangeRate);

        Mockito.verify(exchangeRateRepository, times(1)).setCurrentStatusFalse("source", Currency.USD);
        Mockito.verify(exchangeRateRepository, times(1)).save(exchangeRate);
        Assertions.assertDoesNotThrow(() -> target.save(exchangeRate));
    }

    @Test
    public void findCurrentExchangeRatesTest() {
        String source = "source";
        Currency currency = Currency.USD;
        List<ExchangeRate> expected = Collections.singletonList(new ExchangeRate());
        Mockito.when(exchangeRateRepository.findCurrentExchangeRates(source, currency)).thenReturn(expected);

        List<ExchangeRate> actual = target.findCurrentExchangeRates(source, currency);

        Mockito.verify(exchangeRateRepository, times(1)).findCurrentExchangeRates(source, currency);
        Assertions.assertNotNull(actual);
        assertEquals(expected, actual);
        Assertions.assertDoesNotThrow(() -> target.findCurrentExchangeRates(source, currency));
    }

    @Test
    public void findAllCurrentAndAvgExchangeRatesTest() {
        CurrentAndAvgExchangeRatesDto expected = new CurrentAndAvgExchangeRatesDto();
        Mockito.when(exchangeRateRepository.findAllCurrentAndAvgExchangeRates()).thenReturn(Optional.of(expected));

        CurrentAndAvgExchangeRatesDto actual = target.findAllCurrentAndAvgExchangeRates();

        Mockito.verify(exchangeRateRepository, times(1)).findAllCurrentAndAvgExchangeRates();
        Assertions.assertNotNull(actual);
        assertEquals(expected, actual);
        Assertions.assertDoesNotThrow(() -> target.findAllCurrentAndAvgExchangeRates());

    }

    @Test
    public void findAllCurrentAndAvgExchangeRatesWithEmptyResultTest() {
        Mockito.when(exchangeRateRepository.findAllCurrentAndAvgExchangeRates()).thenReturn(Optional.empty());

        CurrentAndAvgExchangeRatesDto result = target.findAllCurrentAndAvgExchangeRates();

        Mockito.verify(exchangeRateRepository, times(1)).findAllCurrentAndAvgExchangeRates();
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getPrivatUsdExchangeRate());
        Assertions.assertNotNull(result.getPrivatEurExchangeRate());
        Assertions.assertNotNull(result.getMonoUsdExchangeRate());
        Assertions.assertNotNull(result.getMonoEurExchangeRate());
        assertEquals(0.00, result.getAvgUsdBuyExchangeRate(), 0.00);
        assertEquals(0.00, result.getAvgUsdSaleExchangeRate(), 0.00);
        assertEquals(0.00, result.getAvgEurBuyExchangeRate(), 0.00);
        assertEquals(0.00, result.getAvgEurSaleExchangeRate(), 0.00);
        Assertions.assertDoesNotThrow(() -> target.findAllCurrentAndAvgExchangeRates());
    }

    @Test
    public void findExchangeRatesForPeriodTest() {
        LocalDateTime beginning = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 31, 0, 0);
        AvgExchangeRatesForPeriodDto expected
                = new AvgExchangeRatesForPeriodDto(0.85, 0.86, 0.87, 3.0);
        Mockito.when(exchangeRateRepository.findExchangeRatesForPeriod(beginning, end)).thenReturn(Optional.of(expected));

        AvgExchangeRatesForPeriodDto actual = target.findExchangeRatesForPeriod(beginning, end);

        assertEquals(expected, actual);
        Mockito.verify(exchangeRateRepository).findExchangeRatesForPeriod(beginning, end);
        Assertions.assertDoesNotThrow(() -> target.findExchangeRatesForPeriod(beginning, end));
    }


    @Test
    public void findExchangeRatesForPeriodReturnsDefaultTest() {
        LocalDateTime beginning = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 31, 0, 0);
        AvgExchangeRatesForPeriodDto expected =
                new AvgExchangeRatesForPeriodDto(0.0, 0.0, 0.0, 0.0);
        Mockito.when(exchangeRateRepository.findExchangeRatesForPeriod(beginning, end))
                .thenReturn(Optional.empty());

        AvgExchangeRatesForPeriodDto actual = target.findExchangeRatesForPeriod(beginning, end);

        assertEquals(expected, actual);
        Mockito.verify(exchangeRateRepository).findExchangeRatesForPeriod(beginning, end);
        Assertions.assertDoesNotThrow(() -> target.findExchangeRatesForPeriod(beginning, end));
    }

    @Test
    public void findAllCurrentAndAvgExchangeRatesNotNullWithEmptyResultTest() {
        LocalDateTime beginning = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 31, 0, 0);
        Mockito.when(exchangeRateRepository.findExchangeRatesForPeriod(beginning, end))
                .thenReturn(Optional.empty());

        AvgExchangeRatesForPeriodDto result = target.findExchangeRatesForPeriod(beginning, end);

        Mockito.verify(exchangeRateRepository, times(1)).findExchangeRatesForPeriod(beginning, end);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(0.00, result.getAvgUsdBuyExchangeRate(), 0.00);
        Assertions.assertEquals(0.00, result.getAvgUsdSaleExchangeRate(), 0.00);
        Assertions.assertEquals(0.00, result.getAvgEurBuyExchangeRate(), 0.00);
        Assertions.assertEquals(0.00, result.getAvgEurSaleExchangeRate(), 0.00);
    }

}
