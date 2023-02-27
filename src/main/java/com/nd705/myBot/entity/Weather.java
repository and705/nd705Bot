package com.nd705.myBot.entity;

public class Weather {
    private String time;
    private double temperature_2m;
    private double relativehumidity_2m;
    private long precipitation_probability;
    private long cloudcover;
    private double windspeed_10m;
    private double winddirection_10m;
    private double windgusts_10m;

    public Weather() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTemperature_2m() {
        return temperature_2m;
    }

    public void setTemperature_2m(double temperature_2m) {
        this.temperature_2m = temperature_2m;
    }

    public long getPrecipitation_probability() {
        return precipitation_probability;
    }

    public void setPrecipitation_probability(long precipitation_probability) {
        this.precipitation_probability = precipitation_probability;
    }

    public long getCloudcover() {
        return cloudcover;
    }

    public void setCloudcover(long cloudcover) {
        this.cloudcover = cloudcover;
    }

    public double getWindspeed_10m() {
        return windspeed_10m;
    }

    public void setWindspeed_10m(double windspeed_10m) {
        this.windspeed_10m = windspeed_10m;
    }

    public double getWinddirection_10m() {
        return winddirection_10m;
    }

    public void setWinddirection_10m(double winddirection_10m) {
        this.winddirection_10m = winddirection_10m;
    }

    public double getWindgusts_10m() {
        return windgusts_10m;
    }

    public void setWindgusts_10m(double windgusts_10m) {
        this.windgusts_10m = windgusts_10m;
    }

    public double getRelativehumidity_2m() {
        return relativehumidity_2m;
    }

    public void setRelativehumidity_2m(double relativehumidity_2m) {
        this.relativehumidity_2m = relativehumidity_2m;
    }

    @Override
    public String toString() {
        return  "time='" + time + '\'' +
                ", temperature_2m=" + temperature_2m + '\'' +
                ", relativehumidity_2m=" + relativehumidity_2m + '\'' +
                ", precipitation_probability=" + precipitation_probability + '\'' +
                ", cloudcover=" + cloudcover + '\'' +
                ", windspeed_10m=" + windspeed_10m + '\'' +
                ", winddirection_10m=" + winddirection_10m + '\'' +
                ", windgusts_10m=" + windgusts_10m + '\'';
    }
}
