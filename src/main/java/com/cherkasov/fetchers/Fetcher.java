package com.cherkasov.fetchers;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;

@EnableAspectJAutoProxy(proxyTargetClass = true)
public interface Fetcher {
    @Scheduled(cron = "0 0/15 * * * *")
    void fetchAndSaveExchangeRate();

    @PostConstruct
    default void init() {
        fetchAndSaveExchangeRate();
    }


}
