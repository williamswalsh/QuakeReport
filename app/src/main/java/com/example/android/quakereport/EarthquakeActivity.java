/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";

    private static EarthquakeArrayAdapter adapter = null;
    private Earthquake currentEarthquake;
    private String currentEarthquakeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list_view);

        ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();

        // Create a new {@link ArrayAdapter} of earthquakes
        // Point ArrayAdapter to context, ListItem in xml and Data to enumerate.
        adapter = new EarthquakeArrayAdapter(this, earthquakes);

        // Associate the Adapter with the List it will populate
        earthquakeListView.setAdapter(adapter);

        // Create an onItemClick listener to redirect user to new activity with USGS URL
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                currentEarthquake = earthquakes.get(i);

                currentEarthquakeUrl = currentEarthquake.getUrl();

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEarthquakeUrl));
                startActivity(browserIntent);
            }
        });

        // Request earthquake data from URL & Store in list
        // Then update the UI once complete
        new EarthquakeAsyncTask().execute(USGS_REQUEST_URL);

    }

    /**
     * Class used to connect to USGS HTTP API request data and
     * return the earthquake data back to the main thread.
     * Generics -> Params, Progress and Result
     */
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        /**
         * This method begins the background thread which performs operations
         * which may take alot of time, such as network requests.
         * <p>
         * It isn't ok to update the main/UI thread from a different thread
         * therefore we return an list of  {@link Earthquake} objects from the method and
         * we process these object in the onPostExecute() method which
         * runs on the main thread.
         *
         * @param urlStrings Array of Strings which represent URLs
         * @return An {@link Earthquake} object which is used to update the UI
         */
        @Override
        protected List<Earthquake> doInBackground(String... urlStrings) {

            // Don't perform request if there are no urls or if the first URL is null
            // Note that the method only takes one element of the String array
            if (urlStrings.length < 1 || urlStrings[0] == null) {
                return null;
            }
            return QueryUtils.fetchEarthquakeData(urlStrings[0]);
        }

        /**
         * This method occurs after the doInBackground() method.
         * This method runs on the main thread.
         * It is ok to update the UI from the main thread.
         * This method takes the list of  {@link Earthquake} objects which is returned from
         * the doInBackground() method.
         *
         * @param earthquakes A list of earthquake objects.
         */
        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {

            // Clear the adapter of previous earthquake data
            adapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (earthquakes != null && !earthquakes.isEmpty()) {
                adapter.addAll(earthquakes);
            }
        }
    }
}

