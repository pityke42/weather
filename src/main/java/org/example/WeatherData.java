package org.example;
import netscape.javascript.JSObject;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherData {
    //fetching data
    public static JSONObject getWeatherData(String locationName){
        //geolocation api
        JSONArray locationData = getLocationData(locationName);
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
}
