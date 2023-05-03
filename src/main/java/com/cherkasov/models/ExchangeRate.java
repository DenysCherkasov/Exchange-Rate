package com.cherkasov.models;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "exchange_rate")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String source;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private double saleRate;
    private double buyRate;
    private LocalDateTime exchangeRateDateTime;
    private boolean currentStatus;
}
