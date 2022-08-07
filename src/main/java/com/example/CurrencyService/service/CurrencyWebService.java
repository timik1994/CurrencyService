package com.example.CurrencyService.service;


import com.example.CurrencyService.model.CurrencyPairModel;

import java.util.List;

public interface CurrencyWebService {

    Boolean addCurrencyPair(String currencyBase, String secondCurrency);

    List<CurrencyPairModel> getExchangeRateList();

}
