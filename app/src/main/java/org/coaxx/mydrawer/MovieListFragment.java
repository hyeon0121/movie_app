package org.coaxx.mydrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.coaxx.mydrawer.data.MovieData;
import org.coaxx.mydrawer.data.MovieDetail;
import org.coaxx.mydrawer.data.MovieDetailData;
import org.coaxx.mydrawer.data.MovieDetailList;
import org.coaxx.mydrawer.data.ResponseInfo;

import java.util.ArrayList;

public class MovieListFragment extends Fragment {
    MovieDetailFragment movieDetailFragment;
    FragmentCallback callback;

    ImageView MovieImage;
    TextView MovieTitle;
    TextView Rrate;
    TextView MovieGrade;
    MovieData movieData;
    int index;
    ArrayList<MovieDetailData> movieDetailData = new ArrayList<>();

    String tableName = "information";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FragmentCallback){
            callback = (FragmentCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if(callback != null){
            callback = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.movie_list, container, false);

        MovieImage = rootView.findViewById(R.id.movie_thumb);
        MovieTitle = rootView.findViewById(R.id.movie_date);
        Rrate = rootView.findViewById(R.id.reservation_rate);
        MovieGrade = rootView.findViewById(R.id.movie_grade);

        movieData = getArguments().getParcelable("movie_data");

        index = movieData.id;

        StringBuilder titleStr = new StringBuilder(Integer.toString(index));
        titleStr.append(". ");
        titleStr.append(movieData.title);
        MovieTitle.setText(titleStr);


        Glide.with(getContext())
                .load(movieData.image)
                .into(MovieImage);

        StringBuilder rateStr = new StringBuilder("예매율 ");
        rateStr.append(movieData.reservation_rate);
        rateStr.append( "% | ");
        Rrate.setText(rateStr);

        if (movieData.grade == 19) {
            MovieGrade.setText("청소년 관람불가");
        } else {
            StringBuilder gradeStr = new StringBuilder(Integer.toString(movieData.grade));
            gradeStr.append("세 관람가");
            MovieGrade.setText(gradeStr);
        }

        try {
            DatabaseManager.createDetailTable(getContext(), tableName); // 테이블 생성
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "인터넷을 연결해주세요." , Toast.LENGTH_LONG).show();
        }

        Button button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {

                    int status = NetworkStatus.getConnectivityStatus(getContext());
                    if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                        if (AppHelper.requestQueue == null) {
                            AppHelper.requestQueue = Volley.newRequestQueue(getContext());
                        }

                        requestMovieList();
                    }
                    else {
                        try {
                            ArrayList<MovieDetailData> newData =  DatabaseManager.selectDetailData(getContext(), tableName, index);

                            if (newData != null) {
                                MovieDetailFragment newFragment = MovieDetailFragment.newInstance(newData.get(0), movieData.grade);
                                Toast.makeText(getContext(), "인터넷 연결 실패 : 테이블에 저장된 데이터를 불러옵니다.", Toast.LENGTH_LONG).show();
                                getFragmentManager().beginTransaction().add(R.id.container, newFragment).addToBackStack(null).commit();
                            } else {
                                Toast.makeText(getContext(), "테이블 조회 실패 : 인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            Toast.makeText(getContext(), "인터넷을 연결해주세요." , Toast.LENGTH_LONG).show();
                        }

                    }

                }
            }
        });

        return rootView;
    }

    public static MovieListFragment newInstance(MovieData data)
    {
        MovieListFragment myFragment = new MovieListFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("movie_data", data);
        myFragment.setArguments(bundle);

        return myFragment;
    }
    public void requestMovieList() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/readMovie";
        url += "?" + "id=" + index;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }
    public void processResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);
        if (info.code == 200) {
            MovieDetailList movieList = gson.fromJson(response, MovieDetailList.class);
            movieDetailData = new ArrayList<>();
            for(int i = 0; i < movieList.result.size(); i++){
                movieDetailFragment = new MovieDetailFragment();
                MovieDetail movieDetail = movieList.result.get(i);

                if (index == movieDetail.id)
                {
                    movieDetailData.add(i, new MovieDetailData(movieDetail.id, movieDetail.title, movieDetail.thumb, movieDetail.date, movieDetail.genre,
                            movieDetail.duration, movieDetail.like, movieDetail.dislike, movieDetail.reservation_rate, movieDetail.reservation_grade, movieDetail.audience_rating,
                            movieDetail.audience, movieDetail.synopsis, movieDetail.director, movieDetail.actor));

                    try {
                        DatabaseManager.insertDetailData(getContext(),tableName,movieDetail.id, movieDetail.title, movieDetail.thumb, movieDetail.date, movieDetail.genre,
                                movieDetail.duration, movieDetail.like, movieDetail.dislike, movieDetail.reservation_rate, movieDetail.reservation_grade, movieList.result.get(i).grade, movieDetail.audience_rating,
                                movieDetail.audience, movieDetail.synopsis, movieDetail.director, movieDetail.actor);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "인터넷을 연결해주세요." , Toast.LENGTH_LONG).show();
                    }



                    putMovieDataToFragment(movieDetailFragment, movieDetailData.get(i), movieList.result.get(i).grade, movieDetail.photos, movieDetail.videos);
                }
            }

        }

        getFragmentManager().beginTransaction().add(R.id.container, movieDetailFragment).addToBackStack(null).commit();

    }
    public void putMovieDataToFragment(Fragment fragment, MovieDetailData movieDetailData, int grade, String photos, String videos){
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie_data", movieDetailData);
        bundle.putInt("movie_grade", grade);
        bundle.putString("movie_photos", photos);
        bundle.putString("movie_videos", videos);

        fragment.setArguments(bundle);
    }


}
