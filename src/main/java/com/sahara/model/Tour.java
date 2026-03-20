package com.sahara.model;

/** Tour — represents an available tour package. */
public class Tour {
    private String tourId;
    private String tourName;
    private String duration;
    private double price;

    public Tour() {}

    public Tour(String tourId, String tourName, String duration, double price) {
        this.tourId   = tourId;
        this.tourName = tourName;
        this.duration = duration;
        this.price    = price;
    }

    public String getTourId()           { return tourId; }
    public void setTourId(String id)    { this.tourId = id; }

    public String getTourName()         { return tourName; }
    public void setTourName(String n)   { this.tourName = n; }

    public String getDuration()         { return duration; }
    public void setDuration(String d)   { this.duration = d; }

    public double getPrice()            { return price; }
    public void setPrice(double p)      { this.price = p; }

    @Override
    public String toString()            { return tourName + " (" + duration + ") - $" + price; }
}
