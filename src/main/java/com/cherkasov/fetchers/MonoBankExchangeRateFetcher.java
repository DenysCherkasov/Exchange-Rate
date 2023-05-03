package com.cherkasov.fetchers;

import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import com.cherkasov.services.ExchangeRateService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class MonoBankExchangeRateFetcher implements Fetcher {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String URL = "https://api.monobank.ua/bank/currency";
    private static final Gson GSON = new Gson();
    private static final String SOURCE = "MonoBank";
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public MonoBankExchangeRateFetcher(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    @Scheduled(cron = "0 0/15 * * * *")
    public void fetchAndSaveExchangeRate() {
        String json = REST_TEMPLATE.getForObject(URL, String.class);
        List<JsonObject> newExchangeRates = parseAndFilterJson(json);
        List<ExchangeRate> currentUsdExchangeRate =
                exchangeRateService.findCurrentExchangeRates(SOURCE, Currency.USD);
        checkAndSaveNewExchangeRates(currentUsdExchangeRate, newExchangeRates, Currency.USD.toString());
        List<ExchangeRate> currentEurExchangeRate =
                exchangeRateService.findCurrentExchangeRates(SOURCE, Currency.EUR);
        checkAndSaveNewExchangeRates(currentEurExchangeRate, newExchangeRates, Currency.EUR.toString());
    }

    private List<JsonObject> parseAndFilterJson(final String json) {
        return Arrays.stream(GSON.fromJson(json, JsonObject[].class))
                .filter(newExchangeRate -> (newExchangeRate.get("currencyCodeA").getAsInt() == 840 &&
                        newExchangeRate.get("currencyCodeB").getAsInt() == 980) ||
                        (newExchangeRate.get("currencyCodeA").getAsInt() == 978 &&
                                newExchangeRate.get("currencyCodeB").getAsInt() == 980))
                .toList();
    }

    private void checkAndSaveNewExchangeRates(final List<ExchangeRate> currentExchangeRates,
                                              final List<JsonObject> newExchangeRates, String currency) {
        for (JsonObject newExchangeRate : newExchangeRates) {
            if (!currentExchangeRates.isEmpty()
                    && CurrencyByCode(newExchangeRate.get("currencyCodeA").getAsInt())
                    .equals(currentExchangeRates.get(0).getCurrency().toString())
                    && !areRatesEquals(newExchangeRate, currentExchangeRates.get(0))) {
                createExchangeRateFromJsonAndSave(newExchangeRate);
            } else if (currentExchangeRates.isEmpty()
                    && CurrencyByCode(newExchangeRate.get("currencyCodeA").getAsInt()).equals(currency)) {
                createExchangeRateFromJsonAndSave(newExchangeRate);
            }
        }
    }

    private boolean areRatesEquals(final JsonObject newExchangeRate, final ExchangeRate currentExchangeRate) {
        return CurrencyByCode(newExchangeRate.get("currencyCodeA").getAsInt())
                .equals(currentExchangeRate.getCurrency().toString())
                && Double.compare(newExchangeRate.get("rateBuy").getAsDouble(),
                currentExchangeRate.getBuyRate()) == 0
                && Double.compare(newExchangeRate.get("rateSell").getAsDouble(),
                currentExchangeRate.getSaleRate()) == 0;
    }

    private String CurrencyByCode(final int currencyCode) {
        if (currencyCode == 840) {
            return "USD";
        } else {
            return "EUR";
        }
    }

    private void createExchangeRateFromJsonAndSave(final JsonObject newExchangeRate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        Currency currency = Currency.valueOf(CurrencyByCode(newExchangeRate.get("currencyCodeA").getAsInt()));
        exchangeRate.setCurrency(currency);
        exchangeRate.setBuyRate(newExchangeRate.get("rateBuy").getAsDouble());
        exchangeRate.setSaleRate(newExchangeRate.get("rateSell").getAsDouble());
        exchangeRate.setSource(SOURCE);
        exchangeRate.setExchangeRateDateTime(LocalDateTime.now());
        exchangeRate.setCurrentStatus(true);
        exchangeRateService.save(exchangeRate);
    }
}

