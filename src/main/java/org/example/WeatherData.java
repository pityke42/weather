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
    public static JSONObject getWeatherData(String locationName){
        //geolocation api
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build api request url with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude="+ latitude + "&longitude="+longitude +"&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,rain,showers,snowfall,weather_code,surface_pressure";

        try{
            //api call and response
            HttpURLConnection connection = fetchApiResponse(urlString);

            //check response status code
            if(connection.getResponseCode() != 200){
                System.out.println("Error: couldnt connect to api");
                return null;
            }

            //storing result json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()){
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //retrieving coordinates from location name
    public static JSONArray getLocationData(String locationName){
        //replace whitespace in location name with +
        locationName = locationName.replaceAll(" ", "+");

        //api url with location parameter
        String url = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection connection = fetchApiResponse(url);

            //check response
            if(connection.getResponseCode() != 200){
                System.out.println("Error: Cannot connect to API");
            } else{
                //storing api result
                StringBuilder resultJSON = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                //read and store JSON data into string builder
                while (scanner.hasNext()){
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
        }catch (Exception e){
            e.printStackTrace();
        }
        //couldn't find location
        return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //create connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //setting to request method to get
            connection.setRequestMethod("GET");

            //connect to api
            connection.connect();
            return connection;
        }catch (Exception e){
            e.printStackTrace();
        }
        //couldn't make connection
        return null;
    }
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();
        return 0;
    }
    public static String getCurrentTime(){
        //getting current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date and time for api read format -> 2029-01-01T01:01
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH':00'");

        //format, print the current date and time
        String fromattedDateTime = currentDateTime.format(formatter);
        return fromattedDateTime;
    }
}
