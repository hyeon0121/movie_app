package org.coaxx.mydrawer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class CommentItemView extends LinearLayout {
    TextView nameTv;
    TextView timeTv;
    TextView commentTv;
    TextView likeTv;
    RatingBar viewRb;
    Button recommend;
    ImageView writerImage;

    public CommentItemView(Context context) {
        super(context);

        init(context);
    }

    public CommentItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.comment_item_view, this, true);

        nameTv = findViewById(R.id.userName);
        timeTv = findViewById(R.id.timeView);
        commentTv = findViewById(R.id.commentView);
        likeTv = findViewById(R.id.likeView);
        viewRb = findViewById(R.id.viewRating);
        recommend = findViewById(R.id.recommend);
        writerImage = findViewById(R.id.writer_image);
    }

    public void setName(String name) {
        nameTv.setText(name);
    }

    public void setTime(String time) {
        timeTv.setText(time);
    }

    public void setComment(String comment) {
        commentTv.setText(comment);
    }

    public void setLike(int like) {
        likeTv.setText(Integer.toString(like));
    }

    public void setRating(float rating) {
        viewRb.setRating(rating);
    }

    public Button getRecommend() {
        return recommend;
    }

    public void setImage(String image) {
        Glide.with(getContext())
                .load(image)
                .into(writerImage);
    }
}
