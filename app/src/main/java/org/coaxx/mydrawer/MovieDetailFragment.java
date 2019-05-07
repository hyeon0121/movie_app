package org.coaxx.mydrawer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.coaxx.mydrawer.data.CommentItemList;
import org.coaxx.mydrawer.data.MovieDetailData;
import org.coaxx.mydrawer.data.ResponseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieDetailFragment extends Fragment {
    FragmentCallback callback;
    TextView likeCountView;
    Button likeButton;
    TextView dislikeCountView;
    Button dislikeButton;
    ListView listView;
    RatingBar ratingBar;
    RecyclerView recyclerView;
    GalleryAdapter adapter;

    int likeCount;
    int dislikeCount;
    boolean  likeState = false;
    boolean dislikeState = false;
    int status;

    View view;

    TextView movieTitle;
    ImageView movieThumb;
    TextView movieDate;
    TextView genreDuration;
    TextView rgradeRate;
    ImageView gradeImage;
    int movieGrade;
    MovieDetailData movieDetailData;


    TextView audienceRating;
    TextView audience;
    TextView synopsis;
    TextView director;
    TextView actor;

    //Intent allView;
    Button allViewButton;

    String tableName = "comment_view";

    public static final int WRITE_ACTIVITY_RESULT = 101;
    public static final int ALLVIEW_ACTIVITY_RESULT = 1;

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
            callback.onTitleChange(getString(R.string.movie_list));
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.movie_detail1, container, false);

        status = NetworkStatus.getConnectivityStatus(getContext()); // 연결 상태 변수

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.movie_detail));

        final ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);
            }
        });

        movieTitle = view.findViewById(R.id.movie_title);
        movieThumb = view.findViewById(R.id.movie_thumb);
        movieDate = view.findViewById(R.id.movie_date);
        genreDuration = view.findViewById(R.id.genre_duration);
        likeCountView = view.findViewById(R.id.likeCountView);
        dislikeCountView = view.findViewById(R.id.dislikeCountView);
        rgradeRate = view.findViewById(R.id.rgrade_rate);
        audienceRating = view.findViewById(R.id.audience_rating);
        audience = view.findViewById(R.id.audience);
        synopsis = view.findViewById(R.id.synopsis);
        director = view.findViewById(R.id.director);
        actor = view.findViewById(R.id.actor);
        gradeImage = view.findViewById(R.id.grade_image);
        ratingBar = view.findViewById(R.id.ratingBar); //ratingBar
        recyclerView = view.findViewById(R.id.recyclerView);


        movieDetailData = getArguments().getParcelable("movie_data");

        /* 리스트뷰 설정 */
        listView = view.findViewById(R.id.listView);

        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            //갤러리 영역
            ConstraintLayout layout = view.findViewById(R.id.galleryView);
            layout.setVisibility(View.VISIBLE);

            String photos = getArguments().getString("movie_photos");
            String videos = getArguments().getString("movie_videos");
            if (photos != null && videos != null){

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);

                adapter = new GalleryAdapter(getContext());


                String[] photoStr = photos.split(",");
                for( int i = 0;  i < photoStr.length; i++ ){
                    adapter.addItem(new GalleryItem(photoStr[i], true));
                }

                String[] videoStr = videos.split(",");
                for( int i = 0;  i < videoStr.length; i++ ){
                    adapter.addItem(new GalleryItem(videoStr[i], false));
                }

                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(GalleryAdapter.ViewHolder holder, View view, int position) {
                        GalleryItem item = adapter.getItem(position);

                        if (item.isVal()) {
                            Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            intent.putExtra("url", item.getUrl());
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                        }
                    }
                });
            }

            if (AppHelper.requestQueue == null) {
                AppHelper.requestQueue = Volley.newRequestQueue(getContext());
            }

            try {
                DatabaseManager.createReviewTable(getContext(), tableName);
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(getContext(), "인터넷을 연결해주세요." , Toast.LENGTH_LONG).show();
            }

            requestCommentList();
        } else {
            ArrayList<CommentItem> commentItems = DatabaseManager.selectReviewData(getContext(), tableName, movieDetailData.getId(), false);
            if (commentItems != null) {
                Toast.makeText(getContext(), "인터넷 연결 실패 : 테이블에 저장된 데이터를 불러옵니다." , Toast.LENGTH_LONG).show();
                CommentAdapter adapter = new CommentAdapter();
                for (int i = 0; i < 2; i++) {
                    adapter.addItem(commentItems.get(i));
                }

                listView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "테이블 조회 실패 : 인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
            }

        }

        Glide.with(getContext())
                .load(movieDetailData.getThumb())
                .thumbnail(0.1f)
                .into(movieThumb);

        movieTitle.setText(movieDetailData.getTitle());
        StringBuilder dateStr = new StringBuilder(movieDetailData.getDate());
        dateStr.append(" 개봉");
        movieDate.setText(dateStr);

        StringBuilder gdStr = new StringBuilder(movieDetailData.getGenre());
        gdStr.append(" / ");
        gdStr.append(movieDetailData.getDuration());
        gdStr.append(" 분");
        genreDuration.setText(gdStr);

        likeCount = movieDetailData.getLike();
        dislikeCount = movieDetailData.getDislike();

        movieGrade = getArguments().getInt("movie_grade");
        if (movieGrade == 12) {
            gradeImage.setImageResource(R.drawable.ic_12);
        } else if (movieGrade == 15) {
            gradeImage.setImageResource(R.drawable.ic_15);
        } else {
            gradeImage.setImageResource(R.drawable.ic_19);
        }

        StringBuilder likeStr = new StringBuilder(Integer.toString(movieDetailData.getLike()));
        likeCountView.setText(likeStr);
        StringBuilder dislikeStr = new StringBuilder(Integer.toString(movieDetailData.getDislike()));
        dislikeCountView.setText(dislikeStr);

        StringBuilder gradeStr = new StringBuilder(Integer.toString( movieDetailData.getReservation_grade()));
        gradeStr.append("위 ");
        gradeStr.append(Float.toString( movieDetailData.getReservation_rate()));
        gradeStr.append("%");
        rgradeRate.setText(gradeStr);

        StringBuilder audienceRateStr = new StringBuilder(Float.toString(movieDetailData.getAudience_rating()));
        audienceRating.setText(audienceRateStr);

        ratingBar.setRating(movieDetailData.getAudience_rating() / 2);

        StringBuilder audienceStr = new StringBuilder(Integer.toString(movieDetailData.getAudience()));
        audienceStr.append("명");
        audience.setText(audienceStr);

        synopsis.setText(movieDetailData.getSynopsis());
        director.setText(movieDetailData.getDirector());
        actor.setText((CharSequence) movieDetailData.getActor());

        /* 좋아요/싫어요 버튼처리 */
        likeButton = view.findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeState) {
                    likeCount -= 1;
                    decLikeCount();
                }
                else {
                    if(dislikeState) {
                        dislikeState = false;
                        dislikeCount -= 1;
                        decdisLikeCount();
                    }
                    likeCount += 1;
                    incLikeCount();
                }

                likeState = !likeState;

                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/increaseLikeDisLike";

                    StringRequest request = new StringRequest(
                            Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            MovieDetailData data = getArguments().getParcelable("movie_data");
                            params.put("id",  Integer.toString(data.getId()));
                            if (likeState) {
                                params.put("likeyn", "Y");
                            } else {
                                params.put("likeyn", "N");
                            }

                            return params;
                        }

                    };

                    request.setShouldCache(false);
                    AppHelper.requestQueue.add(request);
                }

                likeCountView.setText(Integer.toString(likeCount));
            }
        });

        dislikeButton = view.findViewById(R.id.dislikeButton);
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dislikeState) {
                    dislikeCount -= 1;
                    decdisLikeCount();
                }
                else {
                    if (likeState) {
                        likeState = false;
                        likeCount -= 1;
                        decLikeCount();
                    }
                    dislikeCount += 1;
                    incdisLikeCount();
                }

                dislikeState = !dislikeState;
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/increaseLikeDisLike";

                    StringRequest request = new StringRequest(
                            Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            MovieDetailData data = getArguments().getParcelable("movie_data");
                            params.put("id",  Integer.toString(data.getId()));
                            if (dislikeState) {
                                params.put("dislikeyn", "Y");
                            } else {
                                params.put("dislikeyn", "N");
                            }

                            return params;
                        }

                    };

                    request.setShouldCache(false);
                    AppHelper.requestQueue.add(request);
                }
                dislikeCountView.setText(Integer.toString(dislikeCount));
            }
        });



        /*한줄평 작성하기*/
        Button writeButton = view.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CommentWriteActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MovieDetailData data = getArguments().getParcelable("movie_data");
                    int grade = getArguments().getInt("movie_grade");

                    intent.putExtra("movie_id", data.getId());
                    intent.putExtra("movie_title", data.getTitle());
                    intent.putExtra("movie_grade", grade);
                    startActivityForResult(intent, WRITE_ACTIVITY_RESULT);
            }
        });

        /*한줄평 모두보기*/

        allViewButton = view.findViewById(R.id.allViewButton);
        allViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AllViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    MovieDetailData data = getArguments().getParcelable("movie_data");
                    int grade = getArguments().getInt("movie_grade");

                    intent.putExtra("movie_id", data.getId());
                    intent.putExtra("movie_title", data.getTitle());
                    intent.putExtra("movie_grade", grade);
                    intent.putExtra("rating", data.getAudience_rating());
                    startActivityForResult(intent,ALLVIEW_ACTIVITY_RESULT


                    );
                }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == WRITE_ACTIVITY_RESULT || requestCode == ALLVIEW_ACTIVITY_RESULT) {
            if (intent != null) {
                String result = intent.getStringExtra("result");
                if (result.equals("success")){
                    if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                        requestCommentList();
                    } else {
                        Toast.makeText(getContext(), "인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    public void requestCommentList() {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/readCommentList";

        StringRequest request = new StringRequest(
                Request.Method.POST,
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
                        Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String,String> params = new HashMap<>();
                params.put("id", String.valueOf(movieDetailData.getId()));
                params.put("limit",String.valueOf(2));

                return params;
            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponse(String response) {
        Gson gson = new Gson();
        CommentAdapter adapter = new CommentAdapter();

        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);
        if (info.code == 200) {
            CommentItemList commentItemList = gson.fromJson(response, CommentItemList.class);
            for(int i = 0; i < commentItemList.result.size(); i++){
                CommentItem commentItem = commentItemList.result.get(i);

                if (movieDetailData.getId() == commentItem.getMovieId())
                {
                    adapter.addItem(new CommentItem(commentItem.getId(), commentItem.getWriter(), commentItem.getWriter_image(), commentItem.getTimestamp(),commentItem.getRating(),
                                commentItem.getContents(), commentItem.getRecommend()));

                    //데이터 베이스 저장
                    try {
                        DatabaseManager.insertReviewData(getContext(), tableName, commentItem.getId(), commentItem.getWriter(),commentItem.getWriter_image(), commentItem.getTimestamp(),commentItem.getRating(),
                                commentItem.getContents(), commentItem.getRecommend(), commentItem.getMovieId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        listView.setAdapter(adapter);
    }

    public static MovieDetailFragment newInstance(MovieDetailData data, int grade)
    {
        MovieDetailFragment myFragment = new MovieDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("movie_data", data);
        bundle.putInt("movie_grade", grade);
        myFragment.setArguments(bundle);

        return myFragment;
    }

    public void incLikeCount() {
        likeCountView.setText(String.valueOf(likeCount));
        likeButton.setPressed(true);
        likeButton.setBackgroundResource(R.drawable.ic_thumb_up_selected);
    }
    public void decLikeCount() {
        likeCountView.setText(String.valueOf(likeCount));
        likeButton.setPressed(false);
        likeButton.setBackgroundResource(R.drawable.thumbs_up_selector);
    }

    public void incdisLikeCount() {
        dislikeCountView.setText(String.valueOf(dislikeCount));
        dislikeButton.setPressed(true);
        dislikeButton.setBackgroundResource(R.drawable.ic_thumb_down_selected);
    }
    public void decdisLikeCount() {
        dislikeCountView.setText(String.valueOf(dislikeCount));
        dislikeButton.setPressed(false);
        dislikeButton.setBackgroundResource(R.drawable.thumbs_down_selector);
    }

}