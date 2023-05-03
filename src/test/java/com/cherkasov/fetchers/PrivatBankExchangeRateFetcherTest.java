package com.cherkasov.fetchers;

import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import com.cherkasov.services.ExchangeRateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrivatBankExchangeRateFetcherTest {
    private static final RestTemplate REST_TEMPLATE = mock(RestTemplate.class);
    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    private static final String SOURCE = "PrivatBank";

    @Mock
    private ExchangeRateService service;

    @InjectMocks
    private PrivatBankExchangeRateFetcher target;

    @Test
    public void fetchAndSaveExchangeRateNewExchangeRatesTest() {
        ExchangeRate currentExchangeRate
                = new ExchangeRate("id", SOURCE, Currency.USD, 1.0, 2.0, LocalDateTime.now(), true);
        List<ExchangeRate> currentExchangeRates = new ArrayList<>();
        currentExchangeRates.add(currentExchangeRate);

        Mockito.when(service.findCurrentExchangeRates(SOURCE, Currency.USD)).thenReturn(currentExchangeRates);
        Mockito.when(REST_TEMPLATE.getForObject(URL, String.class))
                .thenReturn("[{\"ccy\":\"EUR\",\"base_ccy\":\"UAH\",\"buy\":\"40.10660\",\"sale\":\"41.84100\"}," +
                        "{\"ccy\":\"USD\",\"base_ccy\":\"UAH\",\"buy\":\"36.56860\",\"sale\":\"37.45318\"}]");

        target.fetchAndSaveExchangeRate();

        Mockito.verify(service, times(2)).save(any());
        Assertions.assertDoesNotThrow(() -> target.fetchAndSaveExchangeRate());
    }

    @Test
    public void fetchAndSaveExchangeRateNewExchangeRatesAreTheSameAsCurrentTest() {
        ExchangeRate currentEurExchangeRate
                = new ExchangeRate("id", SOURCE, Currency.EUR, 41.84100, 40.10660, LocalDateTime.now(), true);
        List<ExchangeRate> currentEurExchangeRates = new ArrayList<>();
        currentEurExchangeRates.add(currentEurExchangeRate);
        Mockito.when(service.findCurrentExchangeRates(SOURCE, Currency.EUR)).thenReturn(currentEurExchangeRates);

        ExchangeRate currentUsdExchangeRate
                = new ExchangeRate("id", SOURCE, Currency.USD, 37.45318, 36.56860, LocalDateTime.now(), true);
        List<ExchangeRate> currentUsdExchangeRates = new ArrayList<>();
        currentUsdExchangeRates.add(currentUsdExchangeRate);
        Mockito.when(service.findCurrentExchangeRates(SOURCE, Currency.USD)).thenReturn(currentUsdExchangeRates);

        Mockito.when(REST_TEMPLATE.getForObject(URL, String.class))
                .thenReturn("[{\"ccy\":\"EUR\",\"base_ccy\":\"UAH\",\"buy\":\"40.10660\",\"sale\":\"41.84100\"}," +
                        "{\"ccy\":\"USD\",\"base_ccy\":\"UAH\",\"buy\":\"36.56860\",\"sale\":\"37.45318\"}]");

        target.fetchAndSaveExchangeRate();

        Mockito.verify(service, never()).save(any());
        Assertions.assertDoesNotThrow(() -> target.fetchAndSaveExchangeRate());
    }

    @Test
    public void fetchAndSaveExchangeRateNoCurrentExchangeRatesTest() {
        List<ExchangeRate> currentExchangeRates = new ArrayList<>();

        Mockito.when(service.findCurrentExchangeRates(SOURCE, Currency.USD)).thenReturn(currentExchangeRates);
        Mockito.when(REST_TEMPLATE.getForObject(URL, String.class))
                .thenReturn("[{\"ccy\":\"EUR\",\"base_ccy\":\"UAH\",\"buy\":\"40.10660\",\"sale\":\"41.84100\"}," +
                        "{\"ccy\":\"USD\",\"base_ccy\":\"UAH\",\"buy\":\"36.56860\",\"sale\":\"37.45318\"}]");
        target.fetchAndSaveExchangeRate();

        Mockito.verify(service, times(2)).save(any());
        Assertions.assertDoesNotThrow(() -> target.fetchAndSaveExchangeRate());

    }

}


