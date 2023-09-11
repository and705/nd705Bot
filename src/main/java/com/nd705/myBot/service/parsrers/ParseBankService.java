package com.nd705.myBot.service.parsrers;

import com.nd705.myBot.entity.weather.Weather;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
@Service
public class ParseBankService {
    public static String parseKgBank(){
        StringBuilder result =new StringBuilder();
        Calendar calendar = new GregorianCalendar();
        result.append(calendar.getTime().toString() + "\n");
        result.append("\n");
        result.append(parseProFinance());
        result.append("\n");
        result.append(parseFincaBank());
        result.append("\n");
        result.append(parseDemirBank());
        result.append("\n");

        return result.toString();

    }
    public static String parseKgBankBakai(){
        StringBuilder result =new StringBuilder();
        result.append(parseBakai());
        result.append("\n");

        return result.toString();

    }






        public static String parseFincaBank(){
            StringBuilder result =new StringBuilder();
        try {
            String fincaSite = "https://fincabank.kg";

            Document document = Jsoup.connect(fincaSite)
                    .get();



            List<Element> valutes = document.select("div.fif-planes-col2");

            result.append("*FINCA BANK*\n");
            //result.append("НАЛИЧНЫЙ\n");
            result.append(String.format("`|%-7s|%-7s|%-7s|\n", valutes.get(0).text(),valutes.get(1).text(),valutes.get(2).text()));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(3).text(),valutes.get(4).text(),valutes.get(5).text()));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(9).text(),valutes.get(10).text(),valutes.get(11).text()));
            //result.append("БЕЗНАЛИЧНЫЙ\n");
            result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(18).text(),valutes.get(19).text(),valutes.get(20).text()));
            result.append(String.format("|%-7s|%-7s|%-7s|`\n", valutes.get(24).text(),valutes.get(25).text(),valutes.get(26).text()));

            float rubusd = Float.parseFloat(valutes.get(20).text())/Float.parseFloat(valutes.get(25).text());
            float usdrub = Float.parseFloat(valutes.get(19).text())/Float.parseFloat(valutes.get(26).text());
            result.append(String.format("RUB->USD: %.2f\n", rubusd));
            result.append(String.format("USD->RUB: %.2f\n", usdrub));

            System.out.println(result);
            return result.toString();

        } catch (Exception e) {
            result.append("не удалось получить данные с https://fincabank.kg");
            return result.toString();
        }

    }



    public static String parseDemirBank(){
        StringBuilder result =new StringBuilder();
        try {
            Document document = Jsoup.connect("https://demirbank.kg/ru/retail").get();
            List<Element> valutes = document.getElementsByTag("td");

            result.append("*DEMIR BANK*\n");
            //result.append("НАЛИЧНЫЙ\n");

            result.append(String.format("`|%-7s|%-7s|%-7s|\n", "ВАЛЮТА","ПОКУПКА","ПРОДАЖА"));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", "USD",valutes.get(5).text(),valutes.get(6).text()));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", "RUB",valutes.get(7).text(),valutes.get(8).text()));
            //result.append("БЕЗНАЛИЧНЫЙ\n");
            result.append(String.format("|%-7s|%-7s|%-7s|\n", "USD",valutes.get(18).text(),valutes.get(19).text()));
            result.append(String.format("|%-7s|%-7s|%-7s|`\n", "RUB",valutes.get(20).text(),valutes.get(21).text()));

            float rubusd = Float.parseFloat(valutes.get(19).text())/Float.parseFloat(valutes.get(20).text());
            float usdrub = Float.parseFloat(valutes.get(18).text())/Float.parseFloat(valutes.get(21).text());
            result.append(String.format("RUB->USD: %.2f\n", rubusd));
            result.append(String.format("USD->RUB: %.2f\n", usdrub));

            System.out.println(result);
            return result.toString();

        } catch (Exception e) {
            result.append("не удалось получить данные с https://demirbank.kg");
            return result.toString();
        }

    }


    public static String parseProFinance(){
        StringBuilder result =new StringBuilder();
        try {
            Document document = Jsoup.connect("https://www.profinance.ru/currency_usd.asp").get();
            List<Element> usd = document.select("td.cell");

            result.append("*MOEX* ");
            result.append(usd.get(16).text()+"\n");
            result.append("*Forex* ");
            result.append(usd.get(15).text()+"\n");

            System.out.println(result);
            return result.toString();

        } catch (Exception e) {
            result.append("не удалось получить данные с https://demirbank.kg");
            return result.toString();
        }

    }

    public static String parseBakai() {
        StringBuilder result =new StringBuilder();
        String json = new String();
        JSONParser parser = new JSONParser();
        try {
            json = Jsoup.connect("https://bakai24.bakai.kg/v1/currency_rates")
                    .ignoreContentType(true)
                    .execute()
                    .body();
        } catch (IOException e) {
            System.out.println("не удалось получить данные от https://bakai24.bakai.kg/v1/currency_rates");
        }


        JSONObject  jsonObject  = null;
        try {
            jsonObject  = (JSONObject ) parser.parse(json);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONArray currencies = (JSONArray) jsonObject.get("currencies");
        JSONObject usd = (JSONObject )currencies.get(0);
        JSONObject rub = (JSONObject )currencies.get(2);



        result.append("*BAKAI BANK*\n");

        result.append(String.format("`|%-7s|%-7s|%-7s|\n", "ВАЛЮТА","ПОКУПКА","ПРОДАЖА"));
        result.append(String.format("|%-7s|%-7s|%-7s|\n", "USD",usd.get("buy").toString(),usd.get("sell").toString()));
        result.append(String.format("|%-7s|%-7s|%-7s|`\n", "RUB",rub.get("buy").toString(),rub.get("sell").toString()));

        float rubusd = Float.parseFloat(usd.get("sell").toString()) / Float.parseFloat(rub.get("buy").toString());
        float usdrub = Float.parseFloat(usd.get("buy").toString()) / Float.parseFloat(rub.get("sell").toString());
        result.append(String.format("RUB->USD: %.2f\n", rubusd));
        result.append(String.format("USD->RUB: %.2f\n", usdrub));

        result.append("онлайн переводы\n");
        result.append(String.format("|%-7s|%-7s|%-7s|\n", "RUB",rub.get("trans_buy").toString(),rub.get("trans_sell").toString()));

        System.out.println(result);
        return result.toString();
    }


    public static String parseMir(){
        StringBuilder result =new StringBuilder();
        try {
            Document document = Jsoup.connect("https://mironline.ru/support/list/kursy_mir/").get();
            Element table = document.select("table").first();
            Elements rows = table.select("tr");
            String som = rows.get(7).select("td").get(1).text().replace(',','.');
            String vnd = rows.get(4).select("td").get(1).text().replace(',','.');

            float somF = Math.round(1 / Float.parseFloat(som) * 1000);
            int vndF = Math.round(1000 / Float.parseFloat(vnd));

            result.append("*МИР*" + "\n");
            result.append("kg: " + som + "(" + somF / 1000 +")"  + "\n" );
            result.append("vnd: " + vnd + "(" + vndF +")" + "\n");


            return result.toString();

        } catch (Exception e) {
            result.append("не удалось получить данные с https://mironline.ru/support/list/kursy_mir/");
            return result.toString();
        }

    }







}
