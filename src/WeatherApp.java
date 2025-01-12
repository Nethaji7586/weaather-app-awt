import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {

    private static final String API_KEY = "810901f3c70f8754158b4bcc606047db";
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String GEOCODING_API_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final int TIMEOUT = 5000; // Timeout for API requests in milliseconds

    /**
     * Fetch weather data for a given location.
     *
     * @param locationName The name of the location to fetch weather data for.
     * @return A JSON object containing weather details or null if an error occurs.
     */
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationCoordinates(locationName);
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: Location not found.");
            return null;
        }

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric", WEATHER_API_URL, latitude, longitude, API_KEY);

        try {
            HttpURLConnection conn = createHttpConnection(urlString);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Error: Could not fetch weather data.");
                return null;
            }

            String responseJson = getResponseData(conn);
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject weatherApiResponse = (JSONObject) parser.parse(responseJson);

            JSONArray forecastList = (JSONArray) weatherApiResponse.get("list");
            int index = findClosestForecastIndex(forecastList);

            return parseWeatherData(forecastList, index);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get coordinates for a given location using the geocoding API.
     *
     * @param locationName The location to search for.
     * @return A JSONArray containing location details or null if an error occurs.
     */
    private static JSONArray getLocationCoordinates(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = String.format("%s?name=%s&count=10&language=en&format=json", GEOCODING_API_URL, locationName);

        try {
            HttpURLConnection conn = createHttpConnection(urlString);
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("Error: Could not fetch location data.");
                return null;
            }

            String responseJson = getResponseData(conn);
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject geocodingApiResponse = (JSONObject) parser.parse(responseJson);

            return (JSONArray) geocodingApiResponse.get("results");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create an HTTP connection to a given URL.
     *
     * @param urlString The URL to connect to.
     * @return An HttpURLConnection instance.
     * @throws IOException If an error occurs while creating the connection.
     */
    private static HttpURLConnection createHttpConnection(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        conn.connect();
        return conn;
    }

    /**
     * Read the response data from an HTTP connection.
     *
     * @param conn The HttpURLConnection instance.
     * @return The response data as a String.
     * @throws IOException If an error occurs while reading the response.
     */
    private static String getResponseData(HttpURLConnection conn) throws IOException {
        StringBuilder result = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNext()) {
                result.append(scanner.nextLine());
            }
        }
        return result.toString();
    }

    /**
     * Find the closest forecast index based on the current time.
     *
     * @param forecastList The list of forecasts.
     * @return The index of the closest forecast.
     */
    private static int findClosestForecastIndex(JSONArray forecastList) {
        String currentTime = getCurrentFormattedTime();
        for (int i = 0; i < forecastList.size(); i++) {
            String forecastTime = (String) ((JSONObject) forecastList.get(i)).get("dt_txt");
            if (forecastTime.equalsIgnoreCase(currentTime)) {
                return i;
            }
        }
        return 0; // Default to the first forecast if no exact match is found
    }

    /**
     * Get the current time formatted as "yyyy-MM-dd HH:00:00".
     *
     * @return The formatted current time.
     */
    private static String getCurrentFormattedTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");
        return currentDateTime.format(formatter);
    }

    /**
     * Extract and parse weather data from a forecast JSON array.
     *
     * @param forecastList The forecast list from the weather API response.
     * @param index        The index of the forecast to extract data from.
     * @return A JSON object containing parsed weather data.
     */
    private static JSONObject parseWeatherData(JSONArray forecastList, int index) {
        JSONObject forecast = (JSONObject) forecastList.get(index);
        JSONObject mainData = (JSONObject) forecast.get("main");
        double temperature = (double) mainData.get("temp");
        long humidity = (long) mainData.get("humidity");

        JSONArray weatherArray = (JSONArray) forecast.get("weather");
        String weatherCondition = (String) ((JSONObject) weatherArray.get(0)).get("description");

        JSONObject windData = (JSONObject) forecast.get("wind");
        double windspeed = (double) windData.get("speed");

        JSONObject weatherData = new JSONObject();
        weatherData.put("temperature", temperature);
        weatherData.put("weather_condition", weatherCondition);
        weatherData.put("humidity", humidity);
        weatherData.put("windspeed", windspeed);

        return weatherData;
    }
}
