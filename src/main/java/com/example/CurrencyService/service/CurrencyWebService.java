package com.example.CurrencyService.service;


import com.example.CurrencyService.model.CurrencyPairModel;

import java.util.Date;
import java.util.List;

public interface CurrencyWebService {

    Float getExchangeRateByDateAndId(int idCurrencyPair, Date rateDate);

    Float getExchangeRateById(int idCurrencyPair);

    Boolean addCurrencyPair(String currencyBase, String secondCurrency);

    List<CurrencyPairModel> getCurrencyPairList();

}
