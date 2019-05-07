package org.coaxx.mydrawer.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieData implements Parcelable {
    public int id;
    public String title;
    public float reservation_rate;
    public int grade;
    public String image;

    public MovieData (Parcel in) {
        readFromParcel(in);
    }
    public MovieData(int id, String title, float reservation_rate, int grade, String image) {
        this.id = id;
        this.title = title;
        this.reservation_rate = reservation_rate;
        this.grade = grade;
        this.image = image;
    }



    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public float getReservation_rate() {
        return reservation_rate;
    }

    public int getGrade() {
        return grade;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeFloat(reservation_rate);
        dest.writeInt(grade);
        dest.writeString(image);

    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        title = in.readString();
        reservation_rate = in.readFloat();
        grade = in.readInt();
        image = in.readString();
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}
