package com.example.CurrencyService.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Currency {

    private String vcharCode; // VcharCode — 3х буквенный код ISO
    private String vname; //Vname — Название валюты
    private Integer vnumCode; //VnumCode — цифровой код ISO
}
