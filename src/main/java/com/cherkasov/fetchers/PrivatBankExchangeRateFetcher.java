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
import java.util.List;

@Component
public class PrivatBankExchangeRateFetcher implements Fetcher {
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    private static final Gson GSON = new Gson();
    private static final String SOURCE = "PrivatBank";
    private final ExchangeRateService exchangeRateService;

    @Autowired
    public PrivatBankExchangeRateFetcher(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    @Scheduled(cron = "0 0/15 * * * *")
    public void fetchAndSaveExchangeRate() {
        String json = REST_TEMPLATE.getForObject(URL, String.class);
        JsonObject[] newExchangeRates = GSON.fromJson(json, JsonObject[].class);
        List<ExchangeRate> currentUsdExchangeRate =
                exchangeRateService.findCurrentExchangeRates(SOURCE, Currency.USD);
        checkAndSaveNewExchangeRates(currentUsdExchangeRate, newExchangeRates, Currency.USD.toString());
        List<ExchangeRate> currentEurExchangeRate =
                exchangeRateService.findCurrentExchangeRates(SOURCE, Currency.EUR);
        checkAndSaveNewExchangeRates(currentEurExchangeRate, newExchangeRates, Currency.EUR.toString());
    }

    private void checkAndSaveNewExchangeRates(final List<ExchangeRate> currentUsdExchangeRates,
                                              final JsonObject[] newExchangeRates, final String currency) {
        for (JsonObject newExchangeRate : newExchangeRates) {
            if (!currentUsdExchangeRates.isEmpty() && newExchangeRate.get("ccy").getAsString()
                    .equals(currentUsdExchangeRates.get(0).getCurrency().toString())
                    && !areRatesEquals(newExchangeRate, currentUsdExchangeRates.get(0))) {
                createExchangeRateFromJsonAndSave(newExchangeRate);
            } else if (currentUsdExchangeRates.isEmpty()
                    && newExchangeRate.get("ccy").getAsString().equals(currency)) {
                createExchangeRateFromJsonAndSave(newExchangeRate);
            }
        }
    }

    private boolean areRatesEquals(final JsonObject newExchangeRate, final ExchangeRate currentExchangeRate) {
        return newExchangeRate.get("ccy").getAsString().equals(currentExchangeRate.getCurrency().toString())
                && Double.compare(newExchangeRate.get("buy").getAsDouble(),
                currentExchangeRate.getBuyRate()) == 0
                && Double.compare(newExchangeRate.get("sale").getAsDouble(),
                currentExchangeRate.getSaleRate()) == 0;
    }

    private void createExchangeRateFromJsonAndSave(final JsonObject newExchangeRate) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrency(Currency.valueOf(newExchangeRate.get("ccy").getAsString()));
        exchangeRate.setBuyRate(newExchangeRate.get("buy").getAsDouble());
        exchangeRate.setSaleRate(newExchangeRate.get("sale").getAsDouble());
        exchangeRate.setSource(SOURCE);
        exchangeRate.setExchangeRateDateTime(LocalDateTime.now());
        exchangeRate.setCurrentStatus(true);
        exchangeRateService.save(exchangeRate);
    }
}

