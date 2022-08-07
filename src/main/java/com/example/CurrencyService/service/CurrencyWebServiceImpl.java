package com.example.CurrencyService.service;

import com.example.CurrencyService.model.CurrencyPairModel;
import com.example.CurrencyService.model.ExchangeRate;
import com.example.CurrencyService.pojo.Currency;
import com.example.CurrencyService.pojo.CurrencyPair;
import com.example.CurrencyService.repository.CurrencyPairRepository;
import com.example.CurrencyService.repository.ExchangeRateRepository;
import com.example.CurrencyService.repository.CbrApiRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class CurrencyWebServiceImpl implements CurrencyWebService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyPairRepository currencyPairRepository;

    private final CbrApiRepository cbrApiRepository;

    private Map<String, Currency> mapCurrencyCatalog;


    @Autowired
    public CurrencyWebServiceImpl(ExchangeRateRepository exchangeRateRepository, CurrencyPairRepository currencyPairRepository, CbrApiRepository cbrApiRepository) {
        this.currencyPairRepository = currencyPairRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.cbrApiRepository = cbrApiRepository;
        this.mapCurrencyCatalog = cbrApiRepository.getMapCurrencyCatalog();
    }


    @Override
    public Float getExchangeRateByDateAndId(int idCurrencyPair, Date rateDate) {
        return null;
    }

    @Override
    public Float getExchangeRateById(int idCurrencyPair) {
        return exchangeRateRepository.getExchangeRateById(idCurrencyPair);
    }

    @Override
    public Boolean addCurrencyPair(String currencyBase, String secondCurrency) {
        return checkCurrencyByMapCurrency(currencyBase, secondCurrency);
    }

    @Override
    public List<CurrencyPairModel> getCurrencyPairList() {
        return currencyPairRepository.findAll();
    }


    private boolean checkCurrencyByMapCurrency(String baseCurrency, String secondCurrency) {

        log.info("Check currencyBase {} and secondCurrency {} , by list currency", baseCurrency, secondCurrency);

        if (!mapCurrencyCatalog.containsKey(baseCurrency) || !mapCurrencyCatalog.containsKey(secondCurrency)) {
            log.info("currency {} or currency {} not found list currency", baseCurrency, secondCurrency);
            return false;
        }


        Currency objectCurrencyBased = mapCurrencyCatalog.get(baseCurrency);
        Currency objectCurrencySecond = mapCurrencyCatalog.get(secondCurrency);

        //TODO Подумать какой id нужно сетить в БД
        int id = objectCurrencyBased.getVnumCode() * objectCurrencySecond.getVnumCode();

        while (currencyPairRepository.findById(id).orElse(null) != null) {
            id += new Random().nextInt();
        }

        String nameCurrencyPair = String.format("%s/%s", objectCurrencyBased.getVname(), objectCurrencySecond.getVname());

        try {
            currencyPairRepository.save(new CurrencyPairModel(id, objectCurrencyBased.getVcharCode(), objectCurrencySecond.getVcharCode(), nameCurrencyPair));
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }


    @Scheduled(fixedRate = 60_000)
    private void fillTableExchangeRate() {

        //TODO получаем все пары из таблицы currency_pair
        List<CurrencyPairModel> listCurrencyPairByDataBase = currencyPairRepository.findAll();

        if (listCurrencyPairByDataBase.size() == 0) {
            return;
        }

        //TODO Получаем список котировок
        Map<String, CurrencyPair> todayCourseCurrencyPairMap = cbrApiRepository.getMapCourseCurrencyPair();

        //TODO рассчитать котировки и вернуть список.
        List<ExchangeRate> exchangeRateList = getListExchangeRate(listCurrencyPairByDataBase, todayCourseCurrencyPairMap);

        //TODO Сохраняем котировки по валютам в таблицу exchange_rate
        exchangeRateRepository.saveAll(exchangeRateList);
    }

    //TODO Дописать логику для рубля
    private List<ExchangeRate> getListExchangeRate(List<CurrencyPairModel> listCurrencyPairByDataBase, Map<String, CurrencyPair> todayCourseCurrencyPairMap) {

        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        //TODO Находим в текущих котировках валюты из БД и получаем по ним котировку

        for (CurrencyPairModel currencyPairModel : listCurrencyPairByDataBase) {

            String baseCharCode = currencyPairModel.getBaseCharCode(); //Базовая валюта
            String quotedCharCode = currencyPairModel.getQuotedCharCode(); //Валюта исчисления

            //TODO получаем по выбранным валютам котировки по отношению к рублю EUR/USD
            CurrencyPair courseBaseCharCode = todayCourseCurrencyPairMap.get(baseCharCode);
            CurrencyPair courseQuotedCharCode = todayCourseCurrencyPairMap.get(quotedCharCode);

            //TODO Рассчитываем курс базовая валюта делаться на валюты исчисления
            Float rateValue = (courseBaseCharCode.getValue() / (float) courseBaseCharCode.getNominal()) / (courseQuotedCharCode.getValue() / (float) courseQuotedCharCode.getNominal());


            ExchangeRate exchangeRate = new ExchangeRate(Timestamp.valueOf(LocalDateTime.now()), rateValue, currencyPairModel.getId());

            exchangeRateList.add(exchangeRate);
        }
        return exchangeRateList;
    }
}
