package com.cherkasov.repositories;

import com.cherkasov.dto.AvgExchangeRatesForPeriodDto;
import com.cherkasov.dto.CurrentAndAvgExchangeRatesDto;
import com.cherkasov.models.Currency;
import com.cherkasov.models.ExchangeRate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CustomizedExchangeRateRepositoryImplTest {
    @Mock
    private EntityManager entityManager;
    @InjectMocks
    private CustomizedExchangeRateRepositoryImpl target;

    @Test
    void findCurrentExchangeRatesTest() {
        String source = "PrivatBank";
        String id = "qwerty";
        Currency currency = Currency.USD;
        List<ExchangeRate> expectedExchangeRates = new ArrayList<>();
        expectedExchangeRates.add(new ExchangeRate(id, source, currency, 1.0, 2.0, LocalDateTime.now(), true));
        TypedQuery<ExchangeRate> queryMock = mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(ExchangeRate.class))).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("source", source)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("currency", currency)).thenReturn(queryMock);
        Mockito.when(queryMock.getResultList()).thenReturn(expectedExchangeRates);

        List<ExchangeRate> actualExchangeRates = target.findCurrentExchangeRates(source, currency);

        Assertions.assertEquals(expectedExchangeRates, actualExchangeRates);
        Assertions.assertNotNull(actualExchangeRates.get(0));
        Mockito.verify(entityManager).createQuery(Mockito.anyString(), Mockito.eq(ExchangeRate.class));
        Mockito.verify(queryMock, times(1)).setParameter("source", source);
        Mockito.verify(queryMock, times(1)).setParameter("currency", currency);
        Mockito.verify(queryMock, times(1)).getResultList();
    }

    @Test
    void findCurrentExchangeRatesNotThrowTest() {
        String source = "PrivatBank";
        Currency currency = Currency.USD;
        TypedQuery<ExchangeRate> queryMock = mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(ExchangeRate.class))).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("source", source)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("currency", currency)).thenReturn(queryMock);

        Assertions.assertDoesNotThrow(() -> target.findCurrentExchangeRates(source, currency));
    }

    @Test
    void setCurrentStatusFalseTest() {
        String source = "PrivatBank";
        Currency currency = Currency.USD;
        Query queryMock = mock(Query.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString())).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("source", source)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("currency", currency)).thenReturn(queryMock);

        target.setCurrentStatusFalse(source, currency);

        Mockito.verify(entityManager, times(1)).createQuery(Mockito.anyString());
        Mockito.verify(queryMock, times(1)).setParameter("source", source);
        Mockito.verify(queryMock, times(1)).setParameter("currency", currency);
        Mockito.verify(queryMock, times(1)).executeUpdate();
    }

    @Test
    void setCurrentStatusFalseNotThrowTest() {
        String source = "PrivatBank";
        Currency currency = Currency.USD;
        Query queryMock = mock(Query.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString())).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("source", source)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("currency", currency)).thenReturn(queryMock);

        Assertions.assertDoesNotThrow(() -> target.setCurrentStatusFalse(source, currency));
    }


    @Test
    void findAllCurrentAndAvgExchangeRatesTest() {
        TypedQuery<CurrentAndAvgExchangeRatesDto> queryMock = mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(CurrentAndAvgExchangeRatesDto.class)))
                .thenReturn(queryMock);
        Mockito.when(queryMock.getResultStream()).thenReturn(Stream.of(new CurrentAndAvgExchangeRatesDto()));

        Optional<CurrentAndAvgExchangeRatesDto> result = target.findAllCurrentAndAvgExchangeRates();

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void findAllCurrentAndAvgExchangeRatesNotThrowTest() {
        TypedQuery<CurrentAndAvgExchangeRatesDto> queryMock = mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(CurrentAndAvgExchangeRatesDto.class)))
                .thenReturn(queryMock);
        Mockito.when(queryMock.getResultStream()).thenReturn(Stream.of(new CurrentAndAvgExchangeRatesDto()));

        Assertions.assertDoesNotThrow(() -> target.findAllCurrentAndAvgExchangeRates());
    }

    @Test
    void findExchangeRatesForPeriodTest() {
        LocalDateTime beginning = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        TypedQuery<AvgExchangeRatesForPeriodDto> queryMock = mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(AvgExchangeRatesForPeriodDto.class)))
                .thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("beginning", beginning)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("end", end)).thenReturn(queryMock);
        Mockito.when(queryMock.getResultStream()).thenReturn(Stream.of(new AvgExchangeRatesForPeriodDto()));

        Optional<AvgExchangeRatesForPeriodDto> result = target.findExchangeRatesForPeriod(beginning, end);

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void findExchangeRatesForPeriodNotThrowTest() {
        LocalDateTime beginning = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        TypedQuery<AvgExchangeRatesForPeriodDto> queryMock = mock(TypedQuery.class);
        Mockito.when(entityManager.createQuery(Mockito.anyString(), Mockito.eq(AvgExchangeRatesForPeriodDto.class)))
                .thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("beginning", beginning)).thenReturn(queryMock);
        Mockito.when(queryMock.setParameter("end", end)).thenReturn(queryMock);
        Mockito.when(queryMock.getResultStream()).thenReturn(Stream.of(new AvgExchangeRatesForPeriodDto()));

        Assertions.assertDoesNotThrow(() -> target.findExchangeRatesForPeriod(beginning, end));
    }

}
