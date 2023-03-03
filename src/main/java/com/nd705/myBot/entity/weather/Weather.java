package com.nd705.myBot.entity.weather;


import com.vdurmont.emoji.EmojiParser;

public class Weather {
    private String time;
    private double temperature_2m;
    private double relativehumidity_2m;
    private long precipitation_probability;
    private long cloudcover;
    private double windspeed_10m;
    private double winddirection_10m;
    private double windgusts_10m;

//    private String temperature_2m_emogi;
//    private String relativehumidity_2m_emogi;
//    private String precipitation_probability_emogi;
//    private String cloudcover_emogi;
//    private String windspeed_10m_emogi;
//    private String winddirection_10m_emogi;
//    private String windgusts_10m_emogi;

    private static String SUN = "\uD83C\uDF1E";
    private static String CLOUD = ":cloud:";
    private static String SUN_BEHIND_SMALL_CLOUD = ":white_sun_small_cloud:";
    private static String SUN_BEHIND_CLOUD = ":partly_sunny:";
    private static String SUN_BEHIND_LARGE_CLOUD = ":white_sun_behind_cloud:";
    private static String SUN_BEHIND_RAIN_CLOUD = "\uD83C\uDF26";
    private static String RAIN_CLOUD = "\uD83C\uDF27";
    private static String THERMOMETR = ":thermometer:";
    private static String WIND_FACE = ":wind_blowing_face:";
    private static String TRIANGULAR_FLAG = ":triangular_flag_on_post:";
    private static String DROPLET = ":droplet:";
    private static String DROPLETS = ":sweat_drops:";


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

    public String getEmogiTemperature_2m() {
        return THERMOMETR;
    }

    public String getEmogiRelativehumidity_2m() {
        return DROPLETS;
    }

    public String getEmogiPrecipitation_probability() {
        return RAIN_CLOUD;
    }

    public String getEmogiCloudcover() {
        if (cloudcover < 20) {
            return SUN;
        } else if (cloudcover >= 20 && cloudcover < 40 && precipitation_probability <= 30) {
            return SUN_BEHIND_SMALL_CLOUD;
        } else if (cloudcover >= 40 && cloudcover < 60 && precipitation_probability <= 30) {
            return SUN_BEHIND_CLOUD;
        } else if (cloudcover >= 60 && cloudcover < 90 && precipitation_probability <= 30) {
            return SUN_BEHIND_LARGE_CLOUD;
        } else if (cloudcover >= 90 && precipitation_probability <= 30) {
            return CLOUD;
        } else if (cloudcover >= 20 && cloudcover < 80 && precipitation_probability > 30) {
            return SUN_BEHIND_RAIN_CLOUD;
        } else if (cloudcover >= 80 && precipitation_probability > 30) {
            return RAIN_CLOUD;
        }
        return "??";
    }

    public String getEmogiWindspeed_10m() {
        return WIND_FACE;
    }

    public String getEmogiWinddirection_10m() {
        if ((winddirection_10m>337.5 && winddirection_10m <=360)||(winddirection_10m>=0 && winddirection_10m<22.5)){
            return "C";
        } else if (winddirection_10m>=22.5 && winddirection_10m<67.5) {
            return "CB";
        } else if (winddirection_10m>=67.5 && winddirection_10m<112.5) {
            return "В";
        }else if (winddirection_10m>=112.5 && winddirection_10m<157.5) {
            return "ЮВ";
        }else if (winddirection_10m>=157.5 && winddirection_10m<202.5) {
            return "Ю";
        }else if (winddirection_10m>=202.5 && winddirection_10m<247.5) {
            return "ЮЗ";
        }else if (winddirection_10m>=247.5 && winddirection_10m<292.5) {
            return "З";
        }else if (winddirection_10m>=292.5 && winddirection_10m<337.5) {
            return "СЗ";
        }
        return "??";
    }

    public String getEmogiWindgusts_10m() {
        return TRIANGULAR_FLAG;
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
