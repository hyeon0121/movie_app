package org.coaxx.mydrawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends BaseAdapter {
    ArrayList<CommentItem> items = new ArrayList<>();
    CommentItemView view = null;
    String tableName = "comment_view";


    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(CommentItem item) {
        items.add(item);
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null) {
            view = new CommentItemView(parent.getContext());
        } else {
            view = (CommentItemView) convertView;
        }

        final int status = NetworkStatus.getConnectivityStatus(context); // 연결 상태 변수

        final CommentItem item = items.get(position);
        view.setName(item.getWriter());
        Long tsLong = System.currentTimeMillis()/1000;
        Long time = item.getTimestamp();
        int seconds = (int) (tsLong - time);
        view.setTime(getTimeAsString(seconds));
        view.setRating(item.getRating());
        view.setComment(item.getContents());
        view.setLike(item.getRecommend());
        if (item.getWriter_image() != null) {
            view.setImage(item.getWriter_image());
        }


        Button recButton = view.findViewById(R.id.recommend);
        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    if (AppHelper.requestQueue == null) {
                        AppHelper.requestQueue = Volley.newRequestQueue(context);
                    }

                    String url = "http://" + AppHelper.host + ":" + AppHelper.port + "/movie/increaseRecommend";
                    StringRequest request = new StringRequest(
                            Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    CommentItem result = items.get(position);
                                    int val = result.getRecommend();
                                    ++val;
                                    result.setRecommend(val);

                                    int cnt = items.get(position).getRecommend();
                                    ++cnt;
                                    view.setLike(cnt);
                                    notifyDataSetChanged();

                                    DatabaseManager.updateReview(context, tableName, cnt-1, items.get(position).getId());
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }

                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("review_id", Integer.toString(items.get(position).getId()));
                            params.put("writer", "kym112");

                            return params;
                        }

                    };

                    request.setShouldCache(false);
                    AppHelper.requestQueue.add(request);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
                }
            }

        });

        return view;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private String getTimeAsString(int seconds) {
        String result = "";
        if (seconds < 60) {
            result = String.format("%s초 전", seconds);
        } else if (seconds < 3600) {
            result =  String.format("%s분 전", seconds/60);
        } else if (seconds < 86400) {
            result =  String.format("%s시간 전", seconds/3600);
        } else if (seconds < 2073600) {
            result =  String.format("%s일 전", seconds/86400);
        } else if (seconds < 62208000) {
            result =  String.format("%s달 전", seconds/2073600);
        }
        return  result;
    }
}
