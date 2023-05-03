package com.cherkasov.repositories;

import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CustomizedExchangeRateRepositoryImpl implements CustomizedExchangeRateRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ExchangeRate> findCurrentExchangeRates(final String source, final Currency currency) {
        return entityManager.createQuery(
                        "SELECT e FROM ExchangeRate e " +
                                "WHERE e.source = :source AND e.currency = :currency " +
                                "AND e.currentStatus = true",
                        ExchangeRate.class)
                .setParameter("source", source)
                .setParameter("currency", currency)
                .getResultList();
    }

    @Override
    @Transactional
    public void setCurrentStatusFalse(final String source, final Currency currency) {
        entityManager.createQuery(
                        "UPDATE ExchangeRate e " +
                                "SET e.currentStatus = false " +
                                "WHERE e.currentStatus = true " +
                                "AND e.source = :source " +
                                "AND e.currency = :currency")
                .setParameter("source", source)
                .setParameter("currency", currency)
                .executeUpdate();
    }


    @Override
    public Optional<CurrentAndAvgExchangeRatesDto> findAllCurrentAndAvgExchangeRates() {
        return entityManager.createQuery("""
                        SELECT new com.cherkasov.dto.CurrentAndAvgExchangeRatesDto
                        (
                            (SELECT er FROM ExchangeRate er
                                 WHERE er.source = 'PrivatBank' AND er.currency = 'USD' AND er.currentStatus = true),
                            (SELECT er1 FROM ExchangeRate er1
                                 WHERE er1.source = 'PrivatBank' AND er1.currency = 'EUR' AND er1.currentStatus = true),
                            (SELECT er2 FROM ExchangeRate er2
                             WHERE er2.source = 'MonoBank' AND er2.currency = 'USD' AND er2.currentStatus = true),
                            (SELECT er3 FROM ExchangeRate er3
                             WHERE er3.source = 'MonoBank' AND er3.currency = 'EUR' AND er3.currentStatus = true),
                            (SELECT AVG(er4.buyRate) FROM ExchangeRate er4
                             WHERE (er4.currency = 'USD' AND er4.currentStatus = true)),
                            (SELECT AVG(er5.saleRate) FROM ExchangeRate er5
                            WHERE (er5.currency = 'USD' AND er5.currentStatus = true)),
                            (SELECT AVG(er6.buyRate) FROM ExchangeRate er6
                             WHERE (er6.currency = 'EUR' AND er6.currentStatus = true)),
                            (SELECT AVG(er7.saleRate) FROM ExchangeRate er7
                             WHERE (er7.currency = 'EUR' AND er7.currentStatus = true)))
                        FROM ExchangeRate e
                        """, CurrentAndAvgExchangeRatesDto.class)
                .getResultStream().findAny();
    }

    @Override
    public Optional<AvgExchangeRatesForPeriodDto> findExchangeRatesForPeriod(final LocalDateTime beginning,
                                                                             final LocalDateTime end) {
        return entityManager.createQuery("""
                        SELECT new com.cherkasov.dto.AvgExchangeRatesForPeriodDto
                        (
                            (SELECT AVG(er1.buyRate) FROM ExchangeRate er1
                             WHERE (er1.currency = 'USD' AND (er1.exchangeRateDateTime BETWEEN :beginning AND :end))),
                            (SELECT AVG(er2.saleRate) FROM ExchangeRate er2
                             WHERE (er2.currency = 'USD' AND (er2.exchangeRateDateTime BETWEEN :beginning AND :end))),
                            (SELECT AVG(er3.buyRate) FROM ExchangeRate er3
                             WHERE (er3.currency = 'EUR' AND (er3.exchangeRateDateTime BETWEEN :beginning AND :end))),
                            (SELECT AVG(er4.saleRate) FROM ExchangeRate er4
                             WHERE (er4.currency = 'EUR' AND (er4.exchangeRateDateTime BETWEEN :beginning AND :end)))
                        ) FROM ExchangeRate e
                        """, AvgExchangeRatesForPeriodDto.class)
                .setParameter("beginning", beginning)
                .setParameter("end", end)
                .getResultStream().findAny();
    }
}
