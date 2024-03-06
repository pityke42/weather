package org.example;
import netscape.javascript.JSObject;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherData {
    //fetching data
    public static JSONObject getWeatherData(String locationName) {
        //geolocation api
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build api request url with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,rain,showers,snowfall,weather_code,surface_pressure,wind_speed_10m";

        try {
            //api call and response
            HttpURLConnection connection = fetchApiResponse(urlString);

            //check response status code
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: couldn't connect to api");
                return null;
            }

            //storing result json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                //read and store in string builder
                resultJson.append(scanner.nextLine());
            }
            scanner.close();

            //close url connection
            connection.disconnect();

            //parse data
            JSONParser parser = new JSONParser();
            JSONObject resultObject = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourlyData = (JSONObject) resultObject.get("hourly");

            //hour data -> indexing current hour
            JSONArray time = (JSONArray) hourlyData.get("time");
            int index = findIndexOfCurrentTime(time);

            //getting temperature
            JSONArray tempData = (JSONArray) hourlyData.get("temperature_2m");
            double temperature = (double) tempData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourlyData.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity
            JSONArray humidityData = (JSONArray) hourlyData.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourlyData.get("wind_speed_10m");
            long windspeed = (long) humidityData.get(index);

            //get precipitation
            JSONArray precipitationData = (JSONArray) hourlyData.get("precipitation");
            double precipitation = (double) precipitationData.get(index);

            //get rain
            JSONArray rainData = (JSONArray) hourlyData.get("rain");
            double rain = (double) rainData.get(index);

            //get showers
            JSONArray showersData = (JSONArray) hourlyData.get("showers");
            double showers = (double) showersData.get(index);

            //get snowfall
            JSONArray snowfallData = (JSONArray) hourlyData.get("snowfall");
            double snowfall = (double) snowfallData.get(index);

            //get surface pressure
            JSONArray surfacePressureData = (JSONArray) hourlyData.get("surface_pressure");
            double surfacePressure = (double) surfacePressureData.get(index);

            //building weather json for frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_code", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            weatherData.put("precipitation", precipitation);
            weatherData.put("rain", rain);
            weatherData.put("showers", showers);
            weatherData.put("snowfall", snowfall);
            weatherData.put("surface_pressure", surfacePressure);
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //retrieving coordinates from location name
    public static JSONArray getLocationData(String locationName) {
        //replace whitespace in location name with +
        locationName = locationName.replaceAll(" ", "+");

        //api url with location parameter
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection connection = fetchApiResponse(url);

            //check response
            if (connection.getResponseCode() != 200) {
                System.out.println("Error: Cannot connect to API");
            } else {
                //storing api result
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                //read and store JSON data into string builder
                while (scanner.hasNext()) {
                    resultJSON.append(scanner.nextLine());
                }
                scanner.close();

                //close url connection
                connection.disconnect();

                //parse JSON String into JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultObject = (JSONObject) parser.parse(String.valueOf(resultJSON));

                //get list of location data
                JSONArray locationData = (JSONArray) resultObject.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            //create connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //setting to request method to get
            connection.setRequestMethod("GET");

            //connect to api
            connection.connect();
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //couldn't make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        //iterate through time list for matching
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                //return index
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        //getting current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date and time for api read format -> 2029-01-01T01:01
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH':00'");

        //format, print the current date and time
        String fromattedDateTime = currentDateTime.format(formatter);
        return fromattedDateTime;
    }

    private static String convertWeatherCode(long weathercode) {
        //converting weather code
        String weatherCondition = "";
        if(weathercode == 0L){
            //clear
            weatherCondition = "Clear";
    } else if (weathercode > 0L && weathercode <= 3L) {
        //cloudy
        weatherCondition = "Cloudy";
    } else if ((weathercode >= 51L && weathercode <= 67L) ||
            (weathercode <= 80L && weathercode <= 90L)) {
        //rain
        weatherCondition = "Rain";
    } else if (weathercode >= 71L && weathercode <= 77L){
        //snow
        weatherCondition = "Snow";
    }

//        switch ((int) weathercode) {
//            case 0 -> weatherCondition = "Clear sky";
//            case 1 -> weatherCondition = "Mainly clear";
//            case 2 -> weatherCondition = "Partly cloudy";
//            case 3 -> weatherCondition = "Overcast";
//            case 45 -> weatherCondition = "Fog";
//            case 48 -> weatherCondition = "Depositing rime fog";
//            case 51 -> weatherCondition = "Light drizzle";
//            case 53 -> weatherCondition = "Moderate drizzle";
//            case 55 -> weatherCondition = "Dense intensity drizzle";
//            case 56 -> weatherCondition = "Light freezing drizzle";
//            case 57 -> weatherCondition = "Dense intensity freezing drizzle";
//            case 61 -> weatherCondition = "Slight rain";
//            case 63 -> weatherCondition = "Moderate rain";
//            case 65 -> weatherCondition = "Heavy intensity rain";
//            case 66 -> weatherCondition = "Light freezing rain";
//            case 67 -> weatherCondition = "Heavy intensity freezing rain";
//            case 71 -> weatherCondition = "Slight snow fall";
//            case 73 -> weatherCondition = "Moderate snow fall";
//            case 75 -> weatherCondition = "Heavy intensity snow fall";
//            case 77 -> weatherCondition = "Snow grains";
//            case 80 -> weatherCondition = "Slight rain showers";
//            case 81 -> weatherCondition = "Moderate rain showers";
//            case 82 -> weatherCondition = "Violent rain showers";
//            case 85 -> weatherCondition = "Slight snow showers";
//            case 86 -> weatherCondition = "Heavy snow showers";
//            case 95 -> weatherCondition = "Thunderstorm";
//            case 96 -> weatherCondition = "Thunderstorm with slight hail";
//            case 99 -> weatherCondition = "Thunderstorm with heavy hail";
//            default -> weatherCondition = "Default weather condition value";
return weatherCondition;

    }
}
