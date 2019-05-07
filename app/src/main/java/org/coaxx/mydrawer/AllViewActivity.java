package org.coaxx.mydrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.coaxx.mydrawer.data.CommentItemList;
import org.coaxx.mydrawer.data.ResponseInfo;

import java.util.ArrayList;

public class AllViewActivity extends AppCompatActivity {
    ArrayList<CommentItem> allItems = new ArrayList<>();
    ListView listView;

    int id;
    int grade;
    TextView commentCount;
    TextView movie_title;
    ImageView movie_grade;

    String tableName = "comment_view";
    int status;
    int listsize;

    private static final int WRITE_ACTIVITY_RESULT = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_view);

        //actionBar 객체 가져옴
        ActionBar actionBar = getSupportActionBar();

        //액션바 '<' 버튼
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_back_selector);

        status = NetworkStatus.getConnectivityStatus(getApplicationContext()); // 연결 상태 변수

        listView = findViewById(R.id.listView);

        movie_grade = findViewById(R.id.movie_grade);
        movie_title = findViewById(R.id.movie_date);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView rateView = findViewById(R.id.rate_view);
        commentCount = findViewById(R.id.comment_count);


        Intent intent = getIntent();
        id = intent.getIntExtra("movie_id",0);

        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            if (AppHelper.requestQueue == null) {
                AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            try {
                DatabaseManager.createReviewTable(getApplicationContext(),tableName);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();

            }

            requestCommentList();
        }
        else {
            ArrayList<CommentItem> commentItems = DatabaseManager.selectReviewData(getApplicationContext(), tableName, id, true);
            CommentAdapter adapter = new CommentAdapter();

            if (commentItems != null) {
                Toast.makeText(getApplicationContext(), "인터넷 연결 실패 : 테이블에 저장된 데이터를 불러옵니다." , Toast.LENGTH_LONG).show();
                for (int i = 0; i < commentItems.size(); i++) {
                    adapter.addItem(commentItems.get(i));
                    listsize = commentItems.size();
                }
            } else {
                Toast.makeText(getApplicationContext(), "테이블 조회 실패 : 인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
            }


            StringBuilder commentStr = new StringBuilder(" (");
            commentStr.append(listsize);
            commentStr.append("명 참여)");
            commentCount.setText(commentStr);

            listView.setAdapter(adapter);

        }

        grade = intent.getIntExtra("movie_grade", 0);
        if (grade == 12) {
            movie_grade.setImageResource(R.drawable.ic_12);
        } else if (grade == 15) {
            movie_grade.setImageResource(R.drawable.ic_15);
        } else {
            movie_grade.setImageResource(R.drawable.ic_19);
        }
        String title = intent.getStringExtra("movie_title");
        movie_title.setText(title);
        Float rating = intent.getFloatExtra("rating", 0.0f);
        ratingBar.setRating(rating);
        rateView.setText(Float.toString(rating));

        Button writeButton = findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentWriteActivity();
            }
        });

    }

    public void showCommentWriteActivity() {
        Intent intent = new Intent(getApplicationContext(), CommentWriteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("movie_id", id);
        intent.putExtra("movie_title", movie_title.getText().toString());
        intent.putExtra("movie_grade", grade);
        startActivityForResult(intent,WRITE_ACTIVITY_RESULT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == WRITE_ACTIVITY_RESULT) {
            if (intent != null) {
                String result = intent.getStringExtra("result");
                if (result.equals("success")){
                    requestCommentList();
                }
            }
        }

    }

    public void requestCommentList() {

        String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/readCommentList";
        url += "?" + "id=" + Integer.toString(id);

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
        CommentAdapter adapter= new CommentAdapter();

        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);
        if (info.code == 200) {
            CommentItemList commentItemList = gson.fromJson(response, CommentItemList.class);
            for(int i = 0; i < commentItemList.result.size(); i++){
                CommentItem commentItem = commentItemList.result.get(i);

                adapter.addItem(new CommentItem(commentItem.getId(), commentItem.getWriter(), commentItem.getWriter_image(), commentItem.getTimestamp(),commentItem.getRating(),
                        commentItem.getContents(), commentItem.getRecommend()));


                try {
                    DatabaseManager.insertReviewData(getApplicationContext(), tableName, commentItem.getId(), commentItem.getWriter(),commentItem.getWriter_image(), commentItem.getTimestamp(),commentItem.getRating(),
                            commentItem.getContents(), commentItem.getRecommend(), commentItem.getMovieId());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();

                }
            }

            StringBuilder commentStr = new StringBuilder(" (");
            commentStr.append(commentItemList.result.size());
            commentStr.append("명 참여)");
            commentCount.setText(commentStr);
        }

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
            {
                Intent data = new Intent();
                data.putExtra("result", "success");

                setResult(RESULT_OK, data);

                finish();

                return true;
            }
        }
        return false;
    }

}
