package com.example.CurrencyService.repository;

import com.example.CurrencyService.pojo.Currency;
import com.example.CurrencyService.pojo.CurrencyPair;
import com.example.CurrencyService.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import okhttp3.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CbrApiRepository {

    @Value("classpath:../main/getListCurrencyPair.xml")
    private Resource res;

    public Map<String, Currency> getMapCurrencyCatalog() {
        Map<String, Currency> mapCurrencyPair = new HashMap<>();
        try {
            String resultXml = getXmlCatalogPair();
            complementingListCurrencyPair(mapCurrencyPair);
            Utils.parsingXMLAndFillingMapCurrencyCatalog(resultXml, mapCurrencyPair);
            return mapCurrencyPair;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, CurrencyPair> getMapCourseCurrencyPair() {
        Map<String, CurrencyPair> stringCurrencyPairMap = new HashMap<>();

        try {
            String resultXml = getXMLCourseCurrencyPair();
            Utils.parsingXMLAndFillingMapCourseCurrencyPair(resultXml, stringCurrencyPairMap);
            return stringCurrencyPairMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void complementingListCurrencyPair(Map<String, Currency> mapCurrencyPair) {
        Currency currency = new Currency("RUB", "Российский рубль", -1);
        mapCurrencyPair.put(currency.getVcharCode(), currency);
    }


    private String getXmlCatalogPair() throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("text/xml; charset=utf-8");

        RequestBody body = RequestBody.create(res.getFile(), mediaType);
        Request request = new Request.Builder()
                .url("http://www.cbr.ru/DailyInfoWebServ/DailyInfo.asmx")
                .method("POST", body)
                .addHeader("Host", "www.cbr.ru")
                .addHeader("Content-Type", "text/xml; charset=utf-8")
                .addHeader("SOAPAction", "\"http://web.cbr.ru/EnumValutesXML\"")
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }


    private String getXMLCourseCurrencyPair() throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url("http://www.cbr.ru/scripts/XML_daily.asp?date_req=")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}
