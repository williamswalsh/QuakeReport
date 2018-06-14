package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    // Create a Log tag
    private static String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return an List of {@link Earthquake} objects
     * to represent a collection of earthquakes.
     */
    public static List<Earthquake> fetchEarthquakeData(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        return extractEarthquakesFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode() + url.toString());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractEarthquakesFromJson(String jsonString) {

        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try block which contains code which may throw a JSON related exception
        try {

            // Get the root JSON Object from the json string
            JSONObject root = new JSONObject(jsonString);

            // Get the JSONArray with the key "features"
            JSONArray features = root.getJSONArray("features");

            // Variables to store JSONObjects & data
            // Variables are defined here so as not to be defined each time per loop
            JSONObject feature, properties;
            double magnitude;
            String place, url;
            String[] locationDataArr;
            long unixTime;
            Earthquake earthquake;

            // Loop to iterate through features JSON Object Array
            for (int i = 0; i < features.length(); i++) {

                // Capture current feature at index i
                feature = features.getJSONObject(i);

                // Capture the sub JSON Object called properties -> Plural as it contains multiple key:value pairs
                properties = feature.getJSONObject("properties");

                // get the value associated with the keys "mag", "place" & "time"
                magnitude = properties.getDouble("mag");
                place = properties.getString("place");
                unixTime = properties.getLong("time");
                url = properties.getString("url");

                earthquake = new Earthquake();
                earthquake.setMagnitude(magnitude);     // Setter incorporates magnitude value validation
                earthquake.setTimeInMillis(unixTime);
                earthquake.setUrl(url);

                // Split if string " of " is detected with <limit> elements
                // ODO Potential null pointer exception - place
                locationDataArr = place.split(" of ", 2);

                // if no split occurs length will remain 1
                if (locationDataArr.length == 1) {

                    // No location offset
                    // Prefix "Near the " infront of the place e.g  Near the North Atlantic Ridge
                    locationDataArr[0] = "Near the " + locationDataArr[0];

                    earthquake.setLocation(locationDataArr[0]);

                } else if (locationDataArr.length == 2) {

                    // Append of back onto the end of the location offset
                    locationDataArr[0] += " of ";
                    earthquake.setLocation(locationDataArr[1]);
                    earthquake.setLocationOffset(locationDataArr[0]);
                }

                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return earthquakes;
    }
}