package com.example.CurrencyService.utils;

import com.example.CurrencyService.pojo.Currency;
import com.example.CurrencyService.pojo.CurrencyPair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class Utils {


    public static void parsingXMLAndFillingMapCurrencyCatalog(String resultXML, Map<String, Currency> mapCurrencyPair) throws ParserConfigurationException, IOException, SAXException {


        NodeList elementValuteDataChildNodes = getNodeList(resultXML);

        for (int i = 0; i < elementValuteDataChildNodes.getLength(); i++) {

            NodeList nodeListEnumValutes = elementValuteDataChildNodes.item(i).getChildNodes();
            Currency currency = getCurrency(nodeListEnumValutes);
            mapCurrencyPair.put(currency.getVcharCode(), currency);
        }
        System.out.println();
    }

    public static void parsingXMLAndFillingMapCourseCurrencyPair(String resultXML, Map<String, CurrencyPair> stringCurrencyPairMap) throws IOException, ParserConfigurationException, SAXException, ParseException {
        NodeList elementValuteDataChildNodes = getNodeList(resultXML);

        for (int i = 0; i < elementValuteDataChildNodes.getLength(); i++) {

            NodeList nodeListEnumValutes = elementValuteDataChildNodes.item(i).getChildNodes();
            CurrencyPair currency = getCurrencyPair(nodeListEnumValutes);
            stringCurrencyPairMap.put(currency.getCharCode(), currency);
        }

    }

    private static NodeList getNodeList(String resultXML) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        InputStream inputStream = new ByteArrayInputStream(resultXML.getBytes(StandardCharsets.UTF_8));
        Document document = documentBuilder.parse(inputStream);
        Element envelope = document.getDocumentElement();

        //TODO Нужно поправить !
        NodeList nodeList = null;
        NodeList elementValuteDataChildNodes = null;
        if (document.getDocumentElement().getNodeName().equals("ValCurs")) {
            nodeList = document.getDocumentElement().getChildNodes();
            elementValuteDataChildNodes = nodeList;
        }

        if (document.getDocumentElement().getNodeName().equals("soap:Envelope")) {
            nodeList = envelope.getElementsByTagName("ValuteData");
            Element elementValuteData = (Element) nodeList.item(0);
            elementValuteDataChildNodes = elementValuteData.getChildNodes();
        }

        return elementValuteDataChildNodes;
    }

    private static Currency getCurrency(NodeList nodeListEnumValutes) {

        String vcharCode = "";
        String vname = "";
        String vnumCode = "";

        for (int y = 0; y < nodeListEnumValutes.getLength(); y++) {
            Node enumValut = nodeListEnumValutes.item(y);
            String nodeName = enumValut.getNodeName();
            String nodeValue = enumValut.getFirstChild().getNodeValue();

            if (nodeName.equals("VcharCode")) {
                vcharCode = nodeValue;
                continue;
            }
            if (nodeName.equals("Vname")) {
                vname = nodeValue.trim();
                continue;
            }

            if (nodeName.equals("VnumCode")) {
                vnumCode = nodeValue.trim();
                continue;
            }

        }

        return new Currency(vcharCode, vname, (!vnumCode.isEmpty() ? Integer.parseInt(vnumCode) : 0));
    }


    private static CurrencyPair getCurrencyPair(NodeList nodeListEnumValutes) throws UnsupportedEncodingException, ParseException {

        String charCode = "";
        String name = "";
        Integer numCode = null;
        Float value = null;
        Integer nominal = null;


        for (int y = 0; y < nodeListEnumValutes.getLength(); y++) {
            Node enumValut = nodeListEnumValutes.item(y);
            String nodeName = enumValut.getNodeName();
            String nodeValue = enumValut.getFirstChild().getNodeValue();

            if (nodeName.equals("CharCode")) {
                charCode = nodeValue;
                continue;
            }
            if (nodeName.equals("Name")) {
                name = new String(nodeValue.trim().getBytes(Charset.forName("Windows-1251")), "UTF-8");
                continue;
            }

            if (nodeName.equals("NumCode")) {
                numCode = nodeValue.trim().isEmpty() ? -1 : Integer.parseInt(nodeValue.trim());
                continue;
            }

            if (nodeName.equals("Nominal")) {
                nominal = NumberFormat.getNumberInstance().parse(nodeValue).intValue();
                continue;
            }

            if (nodeName.equals("Value")) {
                value = NumberFormat.getNumberInstance().parse(nodeValue).floatValue();
                continue;
            }

        }

        return new CurrencyPair(charCode, name, numCode, value, nominal);
    }

}
