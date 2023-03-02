package com.nd705.myBot.entity.weather;




public class City {
    private String cityName;

    private Double latitude;
    private Double longitude;

    public City(String cityName, Double latitude, Double longitude) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return "Город: " + cityName + "\n" +
                "(" + latitude + ", "
                + longitude +
                ")\n";
    }
}
