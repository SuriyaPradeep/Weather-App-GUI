import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//Retrieve weather data from API-this backend logic will fetch the latest weather data from the external API and return it.
//The GUI will display this data to the user.
public class WeatherApp {
    //Fetch weather data for given location
    public static JSONObject getWeatherData(String locationName){
        //Get location coordinates using the geolocation API
        JSONArray locationData=getLocationData(locationName);

        //Extract latitude and longitude data
        JSONObject location=(JSONObject) locationData.get(0);
        double latitude=(double) location.get("latitude");
        double longitude=(double) location.get("longitude");

        //Build API url with location coordinates
        String urlString="https://api.open-meteo.com/v1/forecast?latitude="+latitude+ "&longitude="+longitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";

        try{
            //Call API and get response
            HttpURLConnection conn=fetchApiResponse(urlString);

            //Check for response status
            //If code is 200 is means successful
            if(conn.getResponseCode()!=200){
                System.out.println("Error:Could not connect to API");
                return null;
            }

            //Store the resulting JSON data
            StringBuilder resultJSON=new StringBuilder();
            Scanner scanner=new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJSON.append(scanner.nextLine());
            }

            //Close scanner
            scanner.close();

            //Close url Connection
            conn.disconnect();

            //Parse through our data
            JSONParser parser=new JSONParser();
            JSONObject resultJsonObj=(JSONObject) parser.parse(resultJSON.toString());

            //Retrieve Hourly data
            JSONObject hourly=(JSONObject) resultJsonObj.get("hourly");

            //We want to get current hours data
            //So we need index of our current hour
            JSONArray time=(JSONArray) hourly.get("time");
            int index=findIndexoofCurrentTime(time);

            //Get temperature
            JSONArray temperatureData=(JSONArray) hourly.get("temperature_2m");
            double temperature=(double) temperatureData.get(index);
            //Get weather code
            JSONArray weathercode=(JSONArray) hourly.get("weather_code");
            String weatherCondition=convertWeatherCode((long)weathercode.get(index));


            //Get Humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity=(long) relativeHumidity.get(index);

            //Get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed=(double) windspeedData.get(index);

            //Build weather JSON data object that will be accessed in our front end
            JSONObject weatherData=new JSONObject();
            weatherData.put("temperature",temperature);
            weatherData.put("weather_condition",weatherCondition);
            weatherData.put("humidity",humidity);
            weatherData.put("windspeed",windspeed);

            return weatherData;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //Retrieves geographic coordinates for the given location
    private static JSONArray getLocationData(String locationName){
        //Replace whitespaces in the string to + to adhere to API's request format
        locationName=locationName.replaceAll(" ","+");

        //Build API url with location parameter
        String urlString="https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=10&language=en&format=json";

        try{
            //Call api and get a response
            HttpURLConnection conn=fetchApiResponse(urlString);

            //Check response status
            //200 means successful connection
            if(conn.getResponseCode()!=200){
                System.out.println("Error:Could not connect to API");
                return null;
            }else{
                //Store API result
                StringBuilder resultJson=new StringBuilder();
                Scanner scanner=new Scanner(conn.getInputStream());
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //Close Scanner
                scanner.close();

                //Close url connections
                conn.disconnect();

                //parse the JSON string into a JSON object
                JSONParser parser=new JSONParser();
                JSONObject resultJsonObj=(JSONObject) parser.parse(resultJson.toString());

                //Get the list of location data the API generated from the location name
                JSONArray locationData=(JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //Couldn't able to find the location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            //Attempt to create connection
            URL url=new URL(urlString);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();

            //Set Request method to get
            conn.setRequestMethod("GET");

            //Connect to our API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }
        //Could not able to make connection
        return null;
    }

    private static int findIndexoofCurrentTime(JSONArray timeList){
        String currentTime=getCurrentTime();

        //Iterate through the time list and see which one matches our current time
        for(int i=0;i<timeList.size();i++){
            String time=(String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //Return the index
                return i;
            }
        }
        return 0;
    }
    private static String getCurrentTime(){
        //Get current date and time
        LocalDateTime currentDateTime=LocalDateTime.now();

        //Format date to be 2024-03-24T00:00 (This is how read in the API)
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //Format and print the current date and time
        String formattedDateTime=currentDateTime.format(formatter);
        return formattedDateTime;
    }

    //Convert the weather code to something readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition="";
        if(weathercode == 0L){
            // clear
            weatherCondition = "Clear";
        }else if(weathercode > 0L && weathercode <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            // snow
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
