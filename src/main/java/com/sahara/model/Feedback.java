package com.sahara.model;

import java.time.LocalDate;

/** Feedback — tourist rating and comment for a guide after a booking. */
public class Feedback {
    private int feedbackId;
    private int touristId;
    private int guideId;
    private int bookingId;
    private int rating;
    private String comment;
    private LocalDate feedbackDate;

    // Display fields
    private String touristName;
    private String guideName;

    public Feedback() {}

    public int getFeedbackId()              { return feedbackId; }
    public void setFeedbackId(int id)       { this.feedbackId = id; }

    public int getTouristId()               { return touristId; }
    public void setTouristId(int id)        { this.touristId = id; }

    public int getGuideId()                 { return guideId; }
    public void setGuideId(int id)          { this.guideId = id; }

    public int getBookingId()               { return bookingId; }
    public void setBookingId(int id)        { this.bookingId = id; }

    public int getRating()                  { return rating; }
    public void setRating(int r)            { this.rating = r; }

    public String getComment()              { return comment; }
    public void setComment(String c)        { this.comment = c; }

    public LocalDate getFeedbackDate()      { return feedbackDate; }
    public void setFeedbackDate(LocalDate d){ this.feedbackDate = d; }

    public String getTouristName()          { return touristName; }
    public void setTouristName(String n)    { this.touristName = n; }

    public String getGuideName()            { return guideName; }
    public void setGuideName(String n)      { this.guideName = n; }
}
