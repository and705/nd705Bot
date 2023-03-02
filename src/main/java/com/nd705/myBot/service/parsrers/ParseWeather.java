package com.nd705.myBot.service.parsrers;


import com.nd705.myBot.entity.weather.City;
import com.nd705.myBot.entity.weather.Weather;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseWeather {
    public static Map<String, City> cities = new HashMap<>();
    public static void initialization(){
        cities.put("Danang", new City("Danang", 16.07, 108.22));
        cities.put("Vyborg", new City("Выборг", 60.71, 28.75));
        cities.put("Lahta", new City("Лахтинский разлив", 60.00, 30.18));
        cities.put("Spb", new City("Санкт-Петербург", 59.95, 30.34));
        cities.put("Siverskii", new City("Сиверский", 59.36, 30.10));
        cities.put("Vuoksa", new City("Вуокса", 60.88, 29.83));
    }




    public static Weather[] getWeatherFromOpenMeteo  (double latitude, double longitude){
        String json = new String();
        try {
            json = Jsoup.connect("https://api.open-meteo.com/v1/forecast?" +
                            "latitude="+ latitude + "&" +
                            "longitude=" + longitude +"&" +
                            "hourly=" +
                            "temperature_2m," +
                            "relativehumidity_2m," +
                            "precipitation_probability," +
                            "cloudcover,windspeed_10m," +
                            "winddirection_10m," +
                            "windgusts_10m")
                    .ignoreContentType(true)
                    .execute()
                    .body();


        } catch (IOException e) {
            System.out.println("не удалось получить данные от https://api.open-meteo.com");
        }

        Object JSONobj = null;
        try {
            JSONobj = new JSONParser().parse(json);
        } catch (ParseException e) {
            System.out.println("Couldn't parse weather: "+e.getMessage());
        }
        JSONObject jo = (JSONObject) JSONobj;
        JSONObject hourly = (JSONObject) jo.get("hourly");


        JSONArray time = (JSONArray) hourly.get("time");
        JSONArray temperature_2m = (JSONArray) hourly.get("temperature_2m");
        JSONArray relativehumidity_2m = (JSONArray) hourly.get("relativehumidity_2m");
        JSONArray precipitation_probability = (JSONArray) hourly.get("precipitation_probability");
        JSONArray windspeed_10m = (JSONArray) hourly.get("windspeed_10m");
        JSONArray winddirection_10m = (JSONArray) hourly.get("winddirection_10m");
        JSONArray windgusts_10m = (JSONArray) hourly.get("windgusts_10m");
        JSONArray cloudcover = (JSONArray) hourly.get("cloudcover");

        Weather[] weather = new Weather[temperature_2m.size()];

        for (int i = 0; i < temperature_2m.size(); i++) {
            weather[i] = new Weather();
            weather[i].setTime((String)time.get(i));
            weather[i].setTemperature_2m((double)temperature_2m.get(i));
            weather[i].setRelativehumidity_2m((long)relativehumidity_2m.get(i));
            weather[i].setPrecipitation_probability((long)precipitation_probability.get(i));
            weather[i].setWindspeed_10m((double)windspeed_10m.get(i));
            weather[i].setWinddirection_10m((long)winddirection_10m.get(i));
            weather[i].setWindgusts_10m((double)windgusts_10m.get(i));
            weather[i].setCloudcover((long)cloudcover.get(i));

        }

//        for (Weather w:weather
//        ) {
//            System.out.println(w.toString());
//
//        }
        return weather;
    }

    public static String getOneDayFromWeatherArray(Weather[] weather, int days, int hours){
        StringBuilder OneDayForeCast = new StringBuilder();
        OneDayForeCast.append(String.format("`"));
        OneDayForeCast.append(String.format("|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|\n", "Время","Темп.","Влажн.","Дождь","Облачн.","Ветер","Напр.","Порывы"));
        OneDayForeCast.append(String.format("|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|\n", "чч:мм","град.С","%","%","%","м/с","градю","м/с"));

        for (int i = 0; i < days*24; i+=hours) {
            if (i % 24 == 0) {
                OneDayForeCast.append(weather[i].getTime() + "\n");
            }
            OneDayForeCast.append(String.format("|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|\n",
                    (i) % 24 ,
                    weather[i].getTemperature_2m(),
                    weather[i].getRelativehumidity_2m(),
                    weather[i].getPrecipitation_probability(),
                    weather[i].getCloudcover(),
                    weather[i].getWindspeed_10m(),
                    weather[i].getWinddirection_10m(),
                    weather[i].getWindgusts_10m()));


        }
        OneDayForeCast.append(String.format("`"));
        return OneDayForeCast.toString();

    }

    public static void main(String[] args) throws ParseException {

        Weather[] weather = getWeatherFromOpenMeteo(16.07, 108.22); //Дананг
        System.out.println(getOneDayFromWeatherArray(weather, 3,3));
//        getWeatherFromOpenMeteo(60.71, 28.75); //Выборг
//        getWeatherFromOpenMeteo(60.00, 30.18); //Лахтинский разлив
//        getWeatherFromOpenMeteo(59.95, 30.34); //Санкт-Петербург
//        getWeatherFromOpenMeteo(59.36, 30.10); //Сиверский
//        getWeatherFromOpenMeteo(60.88, 29.83); //Вуокса

    }
}
