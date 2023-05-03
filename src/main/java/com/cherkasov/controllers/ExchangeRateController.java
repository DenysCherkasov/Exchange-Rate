package com.cherkasov.controllers;

import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.services.ExchangeRateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/exchange-rates")
@Api(value = "Exchange Rate Controller", tags = "Exchange Rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @ApiOperation(value = "Find current exchange rates", notes = "Retrieves the current exchange rates",
            response = CurrentAndAvgExchangeRatesDto.class, code = 200)
    @GetMapping("/current")
    public CurrentAndAvgExchangeRatesDto findCurrentExchangeRates() {
        return exchangeRateService.findAllCurrentAndAvgExchangeRates();
    }

    @ApiOperation(value = "Find average archive exchange rates for a period",
            notes = "Retrieves the average archive exchange rates for a period of time",
            response = AvgExchangeRatesForPeriodDto.class, code = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginning", value = "The start of the period to retrieve average exchange rates",
                    dataType = "java.time.LocalDateTime", paramType = "query"),
            @ApiImplicitParam(name = "end", value = "The end of the period to retrieve average exchange rates",
                    dataType = "java.time.LocalDateTime", paramType = "query")
    })
    @GetMapping("/archive")
    public AvgExchangeRatesForPeriodDto findAvgArchiveExchangeRates(@RequestParam(required = false) LocalDateTime beginning,
                                                                    @RequestParam(required = false) LocalDateTime end) {
        if (beginning == null) {
            beginning = LocalDateTime.now().minusDays(1);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        return exchangeRateService.findExchangeRatesForPeriod(beginning, end);
    }
}
