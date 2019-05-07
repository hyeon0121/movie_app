package org.coaxx.mydrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.coaxx.mydrawer.data.ResponseInfo;

import java.util.HashMap;
import java.util.Map;

public class CommentWriteActivity extends AppCompatActivity {
    RatingBar ratingBar;
    EditText contentsInput;
    Intent intent;
    float rating;
    Button saveButton;
    int id;
    Intent data = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_write);

        ratingBar = findViewById(R.id.ratingBar);
        contentsInput = findViewById(R.id.contentsInput);
        ImageView movie_grade = findViewById(R.id.movie_grade);
        TextView movie_title = findViewById(R.id.movie_date);

        intent = getIntent();

        id = intent.getIntExtra("movie_id",0);

        int grade = intent.getIntExtra("movie_grade", 0);
        if (grade == 12) {
            movie_grade.setImageResource(R.drawable.ic_12);
        } else if (grade == 15) {
            movie_grade.setImageResource(R.drawable.ic_15);
        } else {
            movie_grade.setImageResource(R.drawable.ic_19);
        }
        String title = intent.getStringExtra("movie_title");
        movie_title.setText(title);
        rating = ratingBar.getRating();

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    if (AppHelper.requestQueue == null) {
                        AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
                    }

                    String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/createComment";

                    final StringRequest request = new StringRequest(
                            Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Gson gson = new Gson();

                                    ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

                                    if (info.code == 200) {
                                        data.putExtra("result", "success");

                                        setResult(RESULT_OK, data);

                                        finish();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("id",  Integer.toString(id));
                            params.put("writer",  "test");
                            params.put("contents",  contentsInput.getText().toString());
                            params.put("rating", Float.toString(ratingBar.getRating()));

                            return params;
                        }

                    };

                    request.setShouldCache(false);
                    AppHelper.requestQueue.add(request);

                } else {
                    Toast.makeText(getApplicationContext(), "인터넷을 연결해주세요." , Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "취소 버튼을 눌렀습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

}

