package com.example.CurrencyService.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currency_pair")
public class CurrencyPairModel {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "base_charCode", nullable = false, length = 5)
    private String baseCharCode;

    @Column(name = "quoted_charCode", nullable = false, length = 5)
    private String quotedCharCode;

    @Column(name = "description", nullable = false, length = 100)
    private String currencyPairName;
}
