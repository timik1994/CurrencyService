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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class CurrencyWebServiceImpl implements CurrencyWebService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyPairRepository currencyPairRepository;

    private final CbrApiRepository cbrApiRepository;

    private final Map<String, Currency> mapCurrencyCatalog;

    private Map<String, CurrencyPair> todayCourseCurrencyPairMap;
    private LocalDate checkCurrencyPairDate;

    private final SimpleDateFormat simpleDateFormat;


    @Autowired
    public CurrencyWebServiceImpl(ExchangeRateRepository exchangeRateRepository, CurrencyPairRepository currencyPairRepository, CbrApiRepository cbrApiRepository) {
        this.currencyPairRepository = currencyPairRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.cbrApiRepository = cbrApiRepository;
        this.mapCurrencyCatalog = cbrApiRepository.getMapCurrencyCatalog();
        this.simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.checkCurrencyPairDate = LocalDate.now();
    }

    @Override
    public Boolean addCurrencyPair(String currencyBase, String secondCurrency) {
        return checkCurrencyByMapCurrency(currencyBase, secondCurrency);
    }

    @Override
    public List<CurrencyPairModel> getCurrencyPairs() {
        return currencyPairRepository.findAll();
    }

    @Override
    public Float getValueCurseByCurrencyPair(Integer idCurrencyPair) {
        try {
            return exchangeRateRepository.getExchangeRateById(idCurrencyPair).getRateValue();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Float getValueCurseByCurrencyPair(Integer idCurrencyPair, Date rateDate) {

        Map<String, CurrencyPair> currencyPairMap = cbrApiRepository.getMapCourseCurrencyPair(simpleDateFormat.format(rateDate));

        CurrencyPairModel currencyPairModel = currencyPairRepository.findById(idCurrencyPair).orElse(null);
        List<ExchangeRate> exchangeRateList = getListExchangeRate(Arrays.asList(currencyPairModel), currencyPairMap);

        try {
            return exchangeRateList.get(0).getRateValue();
        } catch (NullPointerException e) {
            return null;
        }
    }


    private boolean checkCurrencyByMapCurrency(String baseCurrency, String secondCurrency) {

        log.info("Check currencyBase {} and secondCurrency {} , by list currency", baseCurrency, secondCurrency);

        if (!mapCurrencyCatalog.containsKey(baseCurrency) || !mapCurrencyCatalog.containsKey(secondCurrency)) {
            log.info("currency {} or currency {} not found list currency", baseCurrency, secondCurrency);
            return false;
        }

        Currency objectCurrencyBased = mapCurrencyCatalog.get(baseCurrency);
        Currency objectCurrencySecond = mapCurrencyCatalog.get(secondCurrency);

        CurrencyPairModel currencyPairModel = currencyPairRepository.getCurrencyPairByPairName(baseCurrency, secondCurrency);

        if (currencyPairModel != null) {
            log.info("currency {} or currency {} is exists DB", baseCurrency, secondCurrency);
            return false;
        }

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


    //86_400_000 24 часа
    @Scheduled(fixedRate = 20_000)
    private void fillTableExchangeRate() {

        //TODO получаем все пары из таблицы currency_pair
        List<CurrencyPairModel> listCurrencyPairByDataBase = currencyPairRepository.findAll();

        if (listCurrencyPairByDataBase.size() == 0) {
            return;
        }

        //TODO Получаем новые котировки из ЦБ РФ Map если курс валют еще не заполнен, или время последнего обновления курса было вчера.
        if (todayCourseCurrencyPairMap == null || checkCurrencyPairDate.isBefore(LocalDate.now())) {
            checkCurrencyPairDate = LocalDate.now();
            //TODO Получаем список котировок
            todayCourseCurrencyPairMap = cbrApiRepository.getMapCourseCurrencyPair(simpleDateFormat.format(new Date()));
        }

        //TODO Получаем список котировок
        Map<String, CurrencyPair> todayCourseCurrencyPairMap = cbrApiRepository.getMapCourseCurrencyPair(simpleDateFormat.format(new Date()));

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

            if (quotedCharCode.equals("RUB")) {
                CurrencyPair courseBaseCharCode = todayCourseCurrencyPairMap.get(baseCharCode);
                ExchangeRate exchangeRate = new ExchangeRate(Timestamp.valueOf(LocalDateTime.now()), courseBaseCharCode.getValue(), currencyPairModel.getId());
                exchangeRateList.add(exchangeRate);
                continue;
            }

            if (baseCharCode.equals("RUB")) {

                CurrencyPair courseQuotedCharCode = todayCourseCurrencyPairMap.get(quotedCharCode);
                Float rateValue = courseQuotedCharCode.getNominal() / courseQuotedCharCode.getValue();
                ExchangeRate exchangeRate = new ExchangeRate(Timestamp.valueOf(LocalDateTime.now()), rateValue, currencyPairModel.getId());
                exchangeRateList.add(exchangeRate);
                continue;

            }

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
