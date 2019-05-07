package org.coaxx.mydrawer.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieDetailData implements Parcelable {
    public int id;
    public String title;
    public String thumb;
    public String date;
    public String genre;
    public int duration;
    public int like;
    public int dislike;
    public float reservation_rate;
    public int reservation_grade;
    public float audience_rating;
    public int audience;
    public String synopsis;
    public String director;
    public String actor;

    public MovieDetailData (Parcel in) {
        readFromParcel(in);
    }

    public MovieDetailData(int id, String title, String thumb, String date, String genre, int duration, int like, int dislike, float reservation_rate, int reservation_grade, float audience_rating, int audience, String synopsis, String director, String actor) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.date = date;
        this.genre = genre;
        this.duration = duration;
        this.like = like;
        this.dislike = dislike;
        this.reservation_rate = reservation_rate;
        this.reservation_grade = reservation_grade;
        this.audience_rating = audience_rating;
        this.audience = audience;
        this.synopsis = synopsis;
        this.director = director;
        this.actor = actor;
    }

    public String getThumb() {
        return thumb;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public int getLike() {
        return like;
    }

    public int getDislike() {
        return dislike;
    }

    public float getReservation_rate() {
        return reservation_rate;
    }

    public float getAudience_rating() {
        return audience_rating;
    }

    public int getAudience() {
        return audience;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    public int getReservation_grade() {
        return reservation_grade;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(thumb);
        dest.writeString(date);
        dest.writeString(genre);
        dest.writeInt(duration);
        dest.writeInt(like);
        dest.writeInt(dislike);
        dest.writeFloat(reservation_rate);
        dest.writeInt(reservation_grade);
        dest.writeFloat(audience_rating);
        dest.writeInt(audience);
        dest.writeString(synopsis);
        dest.writeString(director);
        dest.writeString(actor);
    }

    private void readFromParcel(Parcel in) {
       id = in.readInt();
       title = in.readString();
       thumb = in.readString();
       date = in.readString();
       genre = in.readString();
       duration = in.readInt();
       like = in.readInt();
       dislike = in.readInt();
       reservation_rate = in.readFloat();
       reservation_grade = in.readInt();
       audience_rating = in.readFloat();
       audience = in.readInt();
       synopsis = in.readString();
       director = in.readString();
       actor = in.readString();
    }

    public static final Creator<MovieDetailData> CREATOR = new Creator<MovieDetailData>() {
        public MovieDetailData createFromParcel(Parcel in) {
            return new MovieDetailData(in);
        }

        public MovieDetailData[] newArray(int size) {
            return new MovieDetailData[size];
        }
    };
}
