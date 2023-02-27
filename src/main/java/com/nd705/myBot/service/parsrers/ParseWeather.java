package com.nd705.myBot.service.parsrers;


import com.nd705.myBot.entity.Weather;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Iterator;

public class ParseWeather {

    public static Weather[] getWeatherFromOpenMeteo  (double latitude, double longitude){
        String json = new String();
        try {
            json = Jsoup.connect("https://api.open-meteo.com/v1/forecast?" +
                            "latitude=16.07&" +
                            "longitude=108.22&" +
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
            System.out.println("не удалось получить данные с https://api.open-meteo.com");
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




        for (Weather w:weather
        ) {
            System.out.println(w.toString());

        }
        return weather;
    }

    public static void main(String[] args) throws ParseException {

       getWeatherFromOpenMeteo(12, 2);

    }
}
