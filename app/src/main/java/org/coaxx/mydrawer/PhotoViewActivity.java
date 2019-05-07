package org.coaxx.mydrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        //actionBar 객체 가져옴
        ActionBar actionBar = getSupportActionBar();

        //액션바 '<' 버튼
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_back_selector);

        PhotoView photoView = findViewById(R.id.photoView);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        Glide.with(getApplicationContext())
                .load(url)
                .into(photoView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
            {
                finish();

                return true;
            }
        }
        return false;
    }
}
