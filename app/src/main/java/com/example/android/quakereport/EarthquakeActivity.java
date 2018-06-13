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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {
    private Earthquake currentEarthquake;
    private String currentEarthquakeUrl;
    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting the associated layout >> findViewById relies on the chosen layout
        setContentView(R.layout.earthquake_activity);

        // Retrieve earthquake data from parsed json data
        ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list_view);

        // Create a new {@link ArrayAdapter} of earthquakes
        // Point ArrayAdapter to context, ListItem in xml and Data to enumerate.
        EarthquakeArrayAdapter adapter = new EarthquakeArrayAdapter(this, earthquakes);

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


    }
}
