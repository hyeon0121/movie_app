package org.coaxx.mydrawer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.coaxx.mydrawer.data.MovieData;
import org.coaxx.mydrawer.data.MovieDetailData;

import java.util.ArrayList;

public class DatabaseManager {
    private static final String TAG = "DataBaseManager";
    private static SQLiteDatabase database = null;

    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_RESERVATION_RATE = "reservation_rate";
    private static final String MOVIE_GRADE = "grade";
    private static final String MOVIE_IMAGE = "image";
    private static final String MOVIE_THUMB = "thumb";
    private static final String MOVIE_DATE = "date";
    private static final String MOVIE_GENRE = "genre";
    private static final String MOVIE_DURATION = "duration";
    private static final String MOVIE_LIKE= "likeyn";
    private static final String MOVIE_DISLIKE = "dislikeyn";
    private static final String MOVIE_RATE = "rate";
    private static final String MOVIE_RESERVATION_GRADE= "reservationgrade";
    private static final String MOVIE_RATING = "rating";
    private static final String MOVIE_AUDIENCE = "audience";
    private static final String MOVIE_SYNOPSIS = "synopsis";
    private static final String MOVIE_DIRECTOR = "director";
    private static final String MOVIE_ACTOR= "actor";

    private static final String REVIEW_ID = "id";
    private static final String REVIEW_WRITER = "writer";
    private static final String REVIEW_WRITER_IMAGE = "writerimage";
    private static final String REVIEW_TIMESTAMP = "timestamp";
    private static final String REVIEW_RATING = "rating";
    private static final String REVIEW_CONTENTS = "contents";
    private static final String REVIEW_RECOMMEND = "recommend";
    private static final String REVIEW_MOVIE_ID = "mid";

    public static void openDatabase(Context context, String databaseName) {
        println("openDatabase() 호출됨");
        try {
            database = context.openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null);

            if (database != null) {
                println("데이터베이스 " + databaseName+ " 오픈됨");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //DatabaseHelper helper = new DatabaseHelper(this, databaseName, null, 1);
        //database = helper.getWritableDatabase();
    }

    public static void createMovieTable(Context context, String tableName) {
        println("createMovieTable() 호출됨");

        if (database != null) {
            String sql = "create table if not exists " + tableName +
                    "(" + MOVIE_ID + " integer PRIMARY KEY, " +
                    MOVIE_TITLE + " text, " +
                    MOVIE_RESERVATION_RATE + " float, " +
                    MOVIE_GRADE + " integer, " +
                    MOVIE_IMAGE + " text, " +
                    MOVIE_DATE + " date, " +
                    MOVIE_RATING + " float" +
                    ")";
            database.execSQL(sql);
            println("테이블 생성됨");
        }
        else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }
    }

    public static void createDetailTable(Context context, String tableName) {
        println("createDetailTable() 호출됨");

        if (database != null) {
            String sql = "create table if not exists " + tableName +
                    "(" + MOVIE_ID + " integer PRIMARY KEY, " +
                    MOVIE_TITLE + " text, " +
                    MOVIE_THUMB + " text, " +
                    MOVIE_DATE + " text, " +
                    MOVIE_GENRE + " text, " +
                    MOVIE_DURATION + " integer, " +
                    MOVIE_LIKE + " integer, " +
                    MOVIE_DISLIKE + " integer, " +
                    MOVIE_RATE + " float, " +
                    MOVIE_RESERVATION_GRADE + " integer, " +
                    MOVIE_GRADE + " integer, " +
                    MOVIE_RATING + " float, " +
                    MOVIE_AUDIENCE + " integer, " +
                    MOVIE_SYNOPSIS + " text, " +
                    MOVIE_DIRECTOR + " text, " +
                    MOVIE_ACTOR + " text" +
                    ")";
            database.execSQL(sql);
            println("테이블 생성됨");
        }
        else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }
    }

    public static void createReviewTable(Context context, String tableName) {
        println("createDetailTable() 호출됨");

        if (database != null) {
            String sql = "create table if not exists " + tableName +
                    "(" + REVIEW_ID + " integer PRIMARY KEY, " +
                    REVIEW_WRITER + " text, " +
                    REVIEW_WRITER_IMAGE + " text, " +
                    REVIEW_TIMESTAMP + " long, " +
                    REVIEW_RATING + " float, " +
                    REVIEW_CONTENTS + " text, " +
                    REVIEW_RECOMMEND + " int, " +
                    REVIEW_MOVIE_ID + " int" +
                    ")";
            database.execSQL(sql);
            println("테이블 생성됨");
        }
        else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);

            String sql = "create table if not exists " + tableName +
                    "(" + REVIEW_ID + " integer PRIMARY KEY, " +
                    REVIEW_WRITER + " text, " +
                    REVIEW_WRITER_IMAGE + " text, " +
                    REVIEW_TIMESTAMP + " long, " +
                    REVIEW_RATING + " float, " +
                    REVIEW_CONTENTS + " text, " +
                    REVIEW_RECOMMEND + " int, " +
                    REVIEW_MOVIE_ID + " int" +
                    ")";
            database.execSQL(sql);
            println("테이블 생성됨");
        }
    }

    public static void insertReviewData(Context context, String tableName, int id, String writer, String writer_image, Long timestamp, float rating, String contents, int recommend, int mid) {
        println("insertReviewData() 호출됨");

        if (database != null) {
            String sql = "insert or ignore into "+ tableName +
                    "(" + REVIEW_ID + ", " +
                    REVIEW_WRITER + ", " +
                    REVIEW_WRITER_IMAGE + ", " +
                    REVIEW_TIMESTAMP + ", " +
                    REVIEW_RATING + ", " +
                    REVIEW_CONTENTS + ", " +
                    REVIEW_RECOMMEND + ", " +
                    REVIEW_MOVIE_ID+ ") " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] params = {id, writer, writer_image, timestamp, rating, contents, recommend, mid};
            database.execSQL(sql, params);

            println("데이터 추가함");

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }
    }

    public static void insertDetailData(Context context, String tableName, int id, String title, String thumb, String date, String genre, int duration, int like, int dislike,
                                        float reservation_rate, int reservation_grade, int grade, float audience_rating, int audience, String synopsis, String director, String actor) {
        println("insertData() 호출됨");

        if (database != null) {
            String sql = "insert or ignore into "+ tableName +
                    "(" + MOVIE_ID + ", " +
                    MOVIE_TITLE + ", " +
                    MOVIE_THUMB + ", " +
                    MOVIE_DATE + ", " +
                    MOVIE_GENRE + ", " +
                    MOVIE_DURATION + ", " +
                    MOVIE_LIKE + ", " +
                    MOVIE_DISLIKE + ", " +
                    MOVIE_RATE + ", " +
                    MOVIE_RESERVATION_GRADE + ", " +
                    MOVIE_GRADE + ", " +
                    MOVIE_RATING + ", " +
                    MOVIE_AUDIENCE + ", " +
                    MOVIE_SYNOPSIS + ", " +
                    MOVIE_DIRECTOR + ", " +
                    MOVIE_ACTOR + ")" +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)";
            Object[] params = {id, title, thumb, date, genre, duration, like, dislike, reservation_rate, reservation_grade, grade, audience_rating, audience, synopsis, director, actor};
            database.execSQL(sql, params);

            println("데이터 추가함");

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }
    }

    public static void insertMovieData(Context context, String tableName, int id, String title, float reservation_rate, int grade, String image, String date, float rating) {
        println("insertData() 호출됨");

        if (database != null) {
            String sql = "insert or ignore into "+ tableName +
                    "(" + MOVIE_ID + ", " +
                    MOVIE_TITLE + ", " +
                    MOVIE_RESERVATION_RATE + ", " +
                    MOVIE_GRADE + ", " +
                    MOVIE_IMAGE + ", " +
                    MOVIE_DATE + ", " +
                    MOVIE_RATING + ")" +
                    " values(?, ?, ?, ?, ?, ?, ?)";
            Object[] params = {id, title, reservation_rate, grade, image, date, rating};
            database.execSQL(sql, params);

            println("데이터 추가함");

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }
    }


    public static ArrayList<MovieData> selectMovieData(Context context, String tableName) {
        println("selectMovieData() 호출됨");

        ArrayList<MovieData> movieData = new ArrayList<>();

        if (database != null) {
            try {
                String sql = "select * from " + tableName;
                Cursor cursor = database.rawQuery(sql, null);
                println("조회된 데이터 갯수 : " + cursor.getCount());
                if (cursor.getCount() == 0) {
                    movieData = null;
                } else {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        int id = cursor.getInt(0);
                        String title = cursor.getString(1);
                        float reservation_rate  = cursor.getFloat(2);
                        int grade = cursor.getInt(3);
                        String image = cursor.getString(4);

                        movieData.add(i, new MovieData(id,title,reservation_rate,grade,image));
                    }

                    cursor.close();
                }
            } catch (Exception e) {
                println(tableName + " 테이블 조회 오류");
                movieData = null;
            }

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }

        return movieData;
    }

    public static ArrayList<MovieDetailData> selectDetailData(Context context, String tableName, int movieId) {
        println("selectDetailData() 호출됨");
        ArrayList<MovieDetailData> detailData = new ArrayList<>();
        if (database != null) {
            try {
                String sql = "select * from " + tableName + " where id=" + movieId;
                Cursor cursor = database.rawQuery(sql, null);
                println("조회된 데이터 갯수 : " + cursor.getCount());

                if (cursor.getCount() == 0) {
                    detailData = null;
                } else {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        int id = cursor.getInt(0);
                        String title = cursor.getString(1);
                        String thumb  = cursor.getString(2);
                        String date = cursor.getString(3);
                        String genre = cursor.getString(4);
                        int duration = cursor.getInt(5);
                        int like = cursor.getInt(6);
                        int dislike = cursor.getInt(7);
                        float reservation_rate  = cursor.getFloat(8);
                        int reservation_grade = cursor.getInt(9);
                        int grade = cursor.getInt(10);
                        float rating  = cursor.getFloat(11);
                        int audience = cursor.getInt(12);
                        String synopsis = cursor.getString(13);
                        String director = cursor.getString(14);
                        String actor = cursor.getString(15);

                        detailData.add(0,  new MovieDetailData(id, title, thumb, date, genre,
                                duration, like, dislike, reservation_rate, reservation_grade, rating,
                                audience, synopsis, director, actor));

                    }
                    cursor.close();
                }

            } catch (Exception e) {
                println(tableName + " 테이블 조회 오류");
                detailData = null;
            }

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }

        return detailData;
    }
    public static ArrayList<CommentItem> selectReviewData(Context context, String tableName, int movieId, boolean val) {
        println("selectReviewData() 호출됨");
        ArrayList<CommentItem> commentItems = new ArrayList<>();

        if (database != null) {
            try {
                String sql = "select * from " + tableName + " where mid=" + movieId + " order by id desc";
                Cursor cursor = database.rawQuery(sql, null);
                println("조회된 데이터 갯수 : " + cursor.getCount());

                if (cursor.getCount() == 0) {
                    commentItems = null;
                } else {
                    if (val) { //모두보기 화면
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToNext();
                            int id = cursor.getInt(0);
                            String writer = cursor.getString(1);
                            String writer_image = cursor.getString(2);
                            Long timestamp = cursor.getLong(3);
                            float rating = cursor.getFloat(4);
                            String contents = cursor.getString(5);
                            int recommend = cursor.getInt(6);
                            int mid = cursor.getInt(7);

                            CommentItem commentItem = new CommentItem(id, writer, writer_image, timestamp, rating, contents, recommend);
                            commentItems.add(commentItem);
                        }

                    } else { // 상세보기 화면
                        for (int i = 0; i < 2 ; i++) {
                            cursor.moveToNext();
                            int id = cursor.getInt(0);
                            String writer = cursor.getString(1);
                            String writer_image = cursor.getString(2);
                            Long timestamp = cursor.getLong(3);
                            float rating = cursor.getFloat(4);
                            String contents = cursor.getString(5);
                            int recommend = cursor.getInt(6);
                            int mid = cursor.getInt(7);

                            CommentItem commentItem = new CommentItem(id, writer, writer_image, timestamp, rating, contents, recommend);
                            commentItems.add(commentItem);
                        }

                    }
                    cursor.close();
                }

            } catch (Exception e) {
                println(tableName + " 테이블 조회 오류");
                commentItems = null;
            }

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }

        return commentItems;
    }

    public static void updateReview(Context context, String tableName, int recommend, int id) {
        println("updateReview() 호출됨");

        if (database != null) {
            String sql = "update " + tableName + " set recommend=" + recommend + " where id=" + id;
            database.execSQL(sql);

            println("데이터 업데이트 완료 : 추천 수");
        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }

    }

    public static void delete(String tableName) {
        println("delete() 호출됨");

        if (database != null) {
            String sql = "drop table " + tableName;
            database.execSQL(sql);

            println("테이블 삭제 완료");
        }
    }

    public static ArrayList<MovieData> SortMovieData(Context context, String tableName, String type) {
        println("SortMovieData() 호출됨");
        ArrayList<MovieData> movieData = new ArrayList<>();
        if (database != null) {
            try {
                String sql = "select * from " + tableName + " order by " + type + " desc";
                Cursor cursor = database.rawQuery(sql, null);
                println("조회된 데이터 갯수 : " + cursor.getCount());
                if (cursor.getCount() == 0) {
                    movieData = null;
                } else {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToNext();
                        int id = cursor.getInt(0);
                        String title = cursor.getString(1);
                        float reservation_rate  = cursor.getFloat(2);
                        int grade = cursor.getInt(3);
                        String image = cursor.getString(4);


                        movieData.add(i, new MovieData(id,title,reservation_rate,grade,image));

                    }

                    cursor.close();
                }
            } catch (Exception e) {
                println(tableName + " 테이블 조회 오류");
                movieData = null;
            }

        } else {
            println("데이터 베이스를 오픈합니다.");
            openDatabase(context, MainActivity.databaseName);
        }

        return movieData;
    }

    public static void println(String data) {
        Log.d(TAG, data);
    }

}
