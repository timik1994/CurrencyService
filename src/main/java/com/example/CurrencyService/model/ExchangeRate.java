package com.example.CurrencyService.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "exchange_rate")
public class ExchangeRate {

    public ExchangeRate(Timestamp rateDate, Float rateValue, Integer currencyPairID) {
        this.rateDate = rateDate;
        this.rateValue = rateValue;
        this.currencyPairID = currencyPairID;

    }

    @Id
    @Column(name = "id", columnDefinition = "NUMERIC(18,0)")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "id_seq_exchange_rate")
    private Long id;

    @Column(name = "rate_date", nullable = false)
    private Timestamp rateDate;

    @Column(name = "rate_value", nullable = false)
    private Float rateValue;

    @Column(name = "currency_pair_id", nullable = false)
    private Integer currencyPairID;
}
