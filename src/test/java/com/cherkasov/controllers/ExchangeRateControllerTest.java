package com.cherkasov.controllers;

import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.services.ExchangeRateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateControllerTest {

    private ExchangeRateService service;

    private ExchangeRateController target;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(ExchangeRateService.class);
        target = new ExchangeRateController(service);
    }


    @Test
    public void findCurrentExchangeRatesTest() {
        CurrentAndAvgExchangeRatesDto expected = new CurrentAndAvgExchangeRatesDto();
        Mockito.when(service.findAllCurrentAndAvgExchangeRates()).thenReturn(expected);

        CurrentAndAvgExchangeRatesDto actual = target.findCurrentExchangeRates();

        Assertions.assertEquals(expected, actual);
        Mockito.verify(service, times(1)).findAllCurrentAndAvgExchangeRates();
        Assertions.assertNotNull(actual);
    }

    @Test
    public void findCurrentExchangeRatesNotThrowTest() {
        Assertions.assertDoesNotThrow(() -> target.findCurrentExchangeRates());
    }

    @Test
    public void findAvgArchiveExchangeRatesTest() {
        LocalDateTime beginning = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        AvgExchangeRatesForPeriodDto expected = new AvgExchangeRatesForPeriodDto();
        Mockito.when(service.findExchangeRatesForPeriod(beginning, end)).thenReturn(expected);

        AvgExchangeRatesForPeriodDto actual = target.findAvgArchiveExchangeRates(beginning, end);

        Assertions.assertEquals(expected, actual);
        Mockito.verify(service, times(1)).findExchangeRatesForPeriod(beginning, end);
    }

    @Test
    public void findAvgArchiveExchangeRatesNotNullTest() {
        LocalDateTime beginning = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        AvgExchangeRatesForPeriodDto expected = new AvgExchangeRatesForPeriodDto();
        Mockito.when(target.findAvgArchiveExchangeRates(beginning, end)).thenReturn(expected);

        AvgExchangeRatesForPeriodDto result = target.findAvgArchiveExchangeRates(beginning, end);

        Assertions.assertNotNull(result);
        Assertions.assertDoesNotThrow(() -> target.findAvgArchiveExchangeRates(beginning, end));

    }

    @Test
    public void findAvgArchiveExchangeRatesNullNotThrowTest() {
        LocalDateTime beginning = null;
        LocalDateTime end = null;
        Assertions.assertDoesNotThrow(() -> target.findAvgArchiveExchangeRates(beginning, end));
    }

}
