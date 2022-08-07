package com.example.CurrencyService.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyPair {

    private String charCode; // VcharCode — 3х буквенный код ISO
    private String name; //Vname — Название валюты
    private Integer numCode; //VnumCode — цифровой код ISO
    private Float value;//Курс валюты
    private Integer nominal; //Номинал валюты
}
