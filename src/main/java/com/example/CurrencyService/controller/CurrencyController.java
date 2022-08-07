package com.example.CurrencyService.controller;


import java.util.Date;
import java.util.List;

import com.example.CurrencyService.model.CurrencyPairModel;
import io.swagger.v3.oas.annotations.Operation;
import com.example.CurrencyService.service.CurrencyWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CurrencyController {

    private CurrencyWebService currencyWebService;

    @Autowired
    public CurrencyController(CurrencyWebService currencyWebService) {
        this.currencyWebService = currencyWebService;
    }

    @Operation(description = "Принимающий в качестве параметров идентификатор валютной пары (currency_pair_id) и дату (rate_date), возвращающий курс rate_value на заданную дату", tags = "Курс на заданную дату")
    @RequestMapping(value = "/getExchangeRate/{idCurrencyPair}/{rateDate}", method = RequestMethod.GET)
    public ResponseEntity<Float> getExchangeRate(@PathVariable(name = "idCurrencyPair") int idCurrencyPair, @PathVariable(name = "rateDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date rateDate) {
        return new ResponseEntity<>(15f, HttpStatus.OK);
    }

    @Operation(description = "принимающий в качестве параметров код валюты (currency_pair_id), возвращающий самый актуальный курс rate_value", tags = "Самый актуальный курс по коду валюты")
    @RequestMapping(value = "/getExchangeRate/{idCurrency}", method = RequestMethod.GET)
    public ResponseEntity<Float> getExchangeRate(@PathVariable(name = "idCurrency") int idCurrencyPair) {
        return new ResponseEntity<>(15f, HttpStatus.OK);
    }

    @Operation(description = "возвращающий список валютных пар с идентификаторами, по которым можно получить курс", tags = "Получить список валютных пар")
    @RequestMapping(value = "/getExchangeRate", method = RequestMethod.GET)
    public ResponseEntity<List<CurrencyPairModel>> getExchangeRate() {
        return new ResponseEntity<>(currencyWebService.getExchangeRateList(),HttpStatus.OK);
    }

    @Operation(description = "принимающий в теле запроса данные о базовой валюте и валюте исчисления. Должен заносить соответствующую валютную пару в таблицу currency_pair", tags = "Добавление валютной пары")
    @RequestMapping(value = "addCurrencyPair", method = RequestMethod.POST)
    public ResponseEntity<String> addCurrencyPair(@RequestParam String currencyBase, @RequestParam String secondCurrency) {

        boolean result = currencyWebService.addCurrencyPair(currencyBase, secondCurrency);
        String response = String.format("Валютная пара %s/%s", currencyBase, secondCurrency);

        return result ? new ResponseEntity<>(response + " добавлена !", HttpStatus.OK) : new ResponseEntity<>(response + " не добавлена !", HttpStatus.BAD_REQUEST);
    }
}
