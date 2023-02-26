package com.nd705.myBot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ParseService {
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







        public static String parseFincaBank(){
            StringBuilder result =new StringBuilder();
        try {
            Document document = Jsoup.connect("https://fincabank.kg/продукты/обмен-валют/").get();
            List<Element> valutes = document.select("div.fif-planes-col2");

            result.append("FINCA BANK\n");
            //result.append("НАЛИЧНЫЙ\n");
            result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(0).text(),valutes.get(1).text(),valutes.get(2).text()));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(3).text(),valutes.get(4).text(),valutes.get(5).text()));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(9).text(),valutes.get(10).text(),valutes.get(11).text()));
            //result.append("БЕЗНАЛИЧНЫЙ\n");
            result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(18).text(),valutes.get(19).text(),valutes.get(20).text()));
            result.append(String.format("|%-7s|%-7s|%-7s|\n", valutes.get(24).text(),valutes.get(25).text(),valutes.get(26).text()));

            float rubusd = Float.parseFloat(valutes.get(20).text())/Float.parseFloat(valutes.get(25).text());
            float usdrub = Float.parseFloat(valutes.get(19).text())/Float.parseFloat(valutes.get(26).text());
            result.append(String.format("RUB->USD: %.2f\n", rubusd));
            result.append(String.format("USD->RUB: %.2f\n", usdrub));

            System.out.println(result);
            return result.toString();

        } catch (IOException e) {
            result.append("не удалось получить данные с https://fincabank.kg");
            return result.toString();
        }

    }



    public static String parseDemirBank(){
        StringBuilder result =new StringBuilder();
        try {
            Document document = Jsoup.connect("https://demirbank.kg/ru/retail").get();
            List<Element> valutes = document.getElementsByTag("td");

            result.append("DEMIR BANK\n");
            //result.append("НАЛИЧНЫЙ\n");

            result.append(String.format("|%-7s|%-7s|%-7s|\n", "ВАЛЮТА","ПОКУПКА","ПРОДАЖА"));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", "USD",valutes.get(5).text(),valutes.get(6).text()));
            //result.append(String.format("|%-7s|%-7s|%-7s|\n", "RUB",valutes.get(7).text(),valutes.get(8).text()));
            //result.append("БЕЗНАЛИЧНЫЙ\n");
            result.append(String.format("|%-7s|%-7s|%-7s|\n", "USD",valutes.get(18).text(),valutes.get(19).text()));
            result.append(String.format("|%-7s|%-7s|%-7s|\n", "RUB",valutes.get(20).text(),valutes.get(21).text()));

            float rubusd = Float.parseFloat(valutes.get(19).text())/Float.parseFloat(valutes.get(20).text());
            float usdrub = Float.parseFloat(valutes.get(18).text())/Float.parseFloat(valutes.get(21).text());
            result.append(String.format("RUB->USD: %.2f\n", rubusd));
            result.append(String.format("USD->RUB: %.2f\n", usdrub));

            System.out.println(result);
            return result.toString();

        } catch (IOException e) {
            result.append("не удалось получить данные с https://demirbank.kg");
            return result.toString();
        }

    }


    public static String parseProFinance(){
        StringBuilder result =new StringBuilder();
        try {
            Document document = Jsoup.connect("https://www.profinance.ru/currency_usd.asp").get();
            List<Element> usd = document.select("td.cell");

            result.append("MOEX ");
            result.append(usd.get(16).text()+"\n");
            result.append("Forex ");
            result.append(usd.get(15).text()+"\n");

            System.out.println(result);
            return result.toString();

        } catch (IOException e) {
            result.append("не удалось получить данные с https://demirbank.kg");
            return result.toString();
        }

    }


}
