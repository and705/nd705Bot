package com.nd705.myBot.service.parsrers;


import com.nd705.myBot.entity.weather.Weather;
import com.vdurmont.emoji.EmojiParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class ParseWeatherService {


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
                            "windgusts_10m"+
                            "&timezone=auto")
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
            weather[i].setCloudcover((long)cloudcover.get(i));
            weather[i].setRelativehumidity_2m((long)relativehumidity_2m.get(i));
            weather[i].setPrecipitation_probability((long)precipitation_probability.get(i));
            weather[i].setWindspeed_10m((double)windspeed_10m.get(i));
            weather[i].setWinddirection_10m((long)winddirection_10m.get(i));
            weather[i].setWindgusts_10m((double)windgusts_10m.get(i));


        }

//        for (Weather w:weather
//        ) {
//            System.out.println(w.toString());
//
//        }
        return weather;
    }

    public static String getWeatherTableFull(Weather[] weather, int days, int hours){
        StringBuilder OneDayForeCast = new StringBuilder();
        OneDayForeCast.append(String.format("`"));
        OneDayForeCast.append(String.format("|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|\n", "Время","Темп.","Дождь","Облачн.","Ветер","Напр.","Порывы","Влажн."));
        OneDayForeCast.append(String.format("|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|\n", "чч:мм","град.С","%","%","м/с","град.","м/с","%"));

        for (int i = 0; i < days*24; i+=hours) {
            if (i % 24 == 0) {
                OneDayForeCast.append(weather[i].getTime() + "\n");
            }
            OneDayForeCast.append(String.format("|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|%-7s|\n",
                    (i) % 24 ,
                    weather[i].getTemperature_2m(),
                    weather[i].getPrecipitation_probability(),
                    weather[i].getCloudcover(),
                    weather[i].getWindspeed_10m(),
                    weather[i].getWinddirection_10m(),
                    weather[i].getWindgusts_10m(),
                    weather[i].getRelativehumidity_2m()));


        }
        OneDayForeCast.append(String.format("`"));
        return OneDayForeCast.toString();

    }

    public static String getWeatherEmoji(Weather[] weather, int days, int hours){
        StringBuilder forecast = new StringBuilder();

        forecast.append(String.format("`"));
        for (int i = 0; i < days*24; i+=hours) {
            if (i % 24 == 0) {
                forecast.append("\n");
                forecast.append(weather[i].getTime() + "\n");
            }
            forecast.append(String.format("%2s:%1s%4s°%5sм/с %2s%4sм/с\n",
                    (i) % 24,
            weather[i].getEmogiCloudcover(),
            weather[i].getTemperature_2m(),
            weather[i].getEmogiWindspeed_10m()+weather[i].getWindspeed_10m(),
            weather[i].getEmogiWinddirection_10m(),
            weather[i].getEmogiWindgusts_10m() + weather[i].getWindgusts_10m()));

        }
        forecast.append(String.format("`"));
        return EmojiParser.parseToUnicode(forecast.toString());

    }

    public static void main(String[] args) throws ParseException {

        Weather[] weather = getWeatherFromOpenMeteo(16.07, 108.22); //Дананг
        System.out.println(getWeatherEmoji(weather, 3,3));



    }
}
