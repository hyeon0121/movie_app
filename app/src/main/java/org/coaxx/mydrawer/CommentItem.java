package org.coaxx.mydrawer;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentItem implements Parcelable{
    public int id;
    public String writer;
    public int movieId;
    public String writer_image;
    public String time;
    public Long timestamp;
    public float rating;
    public String contents;
    public int recommend;

    public CommentItem (Parcel in) {
        readFromParcel(in);
    }

    public CommentItem(int id, String writer, String writer_image, Long timestamp, float rating, String contents, int recommend) {
        this.id = id;
        this.writer = writer;
        this.writer_image = writer_image;
        this.timestamp = timestamp;
        this.rating = rating;
        this.contents = contents;
        this.recommend = recommend;
    }

    public int getId() {
        return id;
    }

    public String getWriter() {
        return writer;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getWriter_image() {
        return writer_image;
    }

    public String getTime() {
        return time;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public float getRating() {
        return rating;
    }

    public String getContents() {
        return contents;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setWriter_image(String writer_image) {
        this.writer_image = writer_image;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(writer);
        dest.writeInt(movieId);
        dest.writeString(writer_image);
        dest.writeString(time);
        dest.writeLong(timestamp);
        dest.writeFloat(rating);
        dest.writeString(contents);
        dest.writeInt(recommend);

    }

    private void readFromParcel(Parcel in) {
        id = in.readInt();
        writer = in.readString();
        movieId = in.readInt();
        writer_image = in.readString();
        time = in.readString();
        timestamp = in.readLong();
        rating = in.readFloat();
        contents = in.readString();
        recommend = in.readInt();

    }

    public static final Creator<CommentItem> CREATOR = new Creator<CommentItem>() {
        public CommentItem createFromParcel(Parcel in) {
            return new CommentItem(in);
        }

        public CommentItem[] newArray(int size) {
            return new CommentItem[size];
        }
    };

}
