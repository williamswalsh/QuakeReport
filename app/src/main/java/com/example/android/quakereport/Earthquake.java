package com.example.android.quakereport;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class stores the data for a single earthquake instance.
 * <p>
 * Note:
 * USGS -> United States Geological Survey
 */
public class Earthquake {

    // There is no theoretical limit to the magnitude of an earthquake,
    // although it is estimated that an earthquake of magnitude 11 would split the Earth in two. I chose 12.
    private double RICHTER_SCALE_MAX = 12;
    private double RICHTER_SCALE_MIN = 0;

    // USGS Url of the earthquake
    private String url;

    // Location of earthquake, nearest large town/city
    private String location;

    // Distance from nearest relevant town/city
    private String locationOffset;

    // Magnitude of earthquake on the richter scale
    private double magnitude;

    // milliseconds since the unix epoch (Unix Timestamp)
    private long timeInMillis;

    /**
     * Constructs a new {@link Earthquake} object.
     */
    public Earthquake() {
    }

    /**
     * @return The USGS url for this earthquake
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The USGS url for this earthquake
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The location offset String e.g. "15Km NW of"
     */
    public String getLocationOffset() {
        return locationOffset;
    }

    public void setLocationOffset(String locationOffset) {
        this.locationOffset = locationOffset;
    }

    /**
     * @return Location of earthquake
     */
    public String getlocation() {
        return location;
    }

    /**
     * @return The Richter Scale magnitude of the earthquake
     */
    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        // Acceptable Range 0-12
        // Only sets values within range, default value otherwise
        if (magnitude > RICHTER_SCALE_MIN && magnitude < RICHTER_SCALE_MAX) {
            this.magnitude = magnitude;
        } else {
            Log.d("Earthquake", "setMagnitude: Magnitude value passed to setMagnitude is out of range. (>12 or <0) ");

        }
    }

    /**
     * @return The time in milliseconds (since the epoch) when the earthquake occurred
     */
    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "RICHTER_SCALE_MAX=" + RICHTER_SCALE_MAX +
                ", RICHTER_SCALE_MIN=" + RICHTER_SCALE_MIN +
                ", location='" + location + '\'' +
                ", locationOffset='" + locationOffset + '\'' +
                ", magnitude=" + magnitude +
                ", timeInMillis=" + timeInMillis +
                '}';
    }

    /**
     * Gets the time in a legible String form
     *
     * @return Returns the time in String format in the SimpleDateFormat("mm:ss"); e.g. 12:16
     */
    public String getTimeString() {
        Date date = new Date(this.timeInMillis);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("h:mm a");
        String displayTime = dateFormatter.format(date);

        return displayTime;
    }

    /**
     * Gets the date in a legible String form
     *
     * @return Returns the date in String format in the SimpleDateFormat("MMM DD, yyyy"); e.g. JAN 15, 2010
     */
    public String getDateString() {
        Date date = new Date(this.timeInMillis);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM DD, yyyy");
        String displayDate = dateFormatter.format(date);

        return displayDate;
    }
}
