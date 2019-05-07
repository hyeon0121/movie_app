package org.coaxx.mydrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.coaxx.mydrawer.data.MovieData;
import org.coaxx.mydrawer.data.MovieInfo;
import org.coaxx.mydrawer.data.MovieList;
import org.coaxx.mydrawer.data.ResponseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , FragmentCallback{

    public Toolbar toolbar;
    Button orderButton;

    ViewPager pager;

    String tableName = "movie_data";
    public static String databaseName = "movie";

    MoviePagerAdapter adapter = new MoviePagerAdapter(getSupportFragmentManager());

    MovieListFragment fragment1;
    String title;
    String image;
    ArrayList<MovieData> movieData;

    Animation translateUp;
    Animation translateDown;

    View menuContainer;
    boolean isShown = false;

    public static int typeOne = 1;
    public static int typeTwo = 2;
    public static int typeThree = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.movie_list));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.ic_hamburger_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            DatabaseManager.openDatabase(getApplicationContext(), databaseName); //데이터베이스 오픈
            DatabaseManager.createMovieTable(getApplicationContext(), tableName); // 테이블 생성
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "데이터 베이스 오류 : 인터넷을 연결해주세요." , Toast.LENGTH_LONG).show();
        }

        //애니메이션
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);

        translateUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuContainer.setVisibility(View.INVISIBLE);
                isShown = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        menuContainer = findViewById(R.id.menuContainer);

        //페이저
        pager = findViewById(R.id.pager);

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            if (AppHelper.requestQueue == null) {
                AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            requestMovieList(typeOne);
        }
        else {
            ArrayList<MovieData> movieData = DatabaseManager.selectMovieData(getApplicationContext(), tableName);
            if (movieData != null) {
                Toast.makeText(getApplicationContext(), "인터넷 연결 실패 : 테이블에 저장된 데이터를 불러옵니다.", Toast.LENGTH_LONG).show();
                MovieListFragment fragment;
                for (int i = 0; i < movieData.size(); i++) {
                    fragment = MovieListFragment.newInstance(movieData.get(i));
                    adapter.addItem(fragment);
                }

                pager.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), "테이블 조회 실패 : 인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
            }


        }


    }
    public void requestMovieList(final int type) {
        String url = "http://" + AppHelper.host + ":" + AppHelper.port+ "/movie/readMovieList";

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
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("type", String.valueOf(type));

                return params;
            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponse(String response) {
        Gson gson = new Gson();

        ResponseInfo info = gson.fromJson(response, ResponseInfo.class);

        if (info.code == 200) {
            MovieList list = gson.fromJson(response, MovieList.class);
            ArrayList<Fragment> fragments = new ArrayList<>();
            movieData = new ArrayList<>();

            MoviePagerAdapter adapter = new MoviePagerAdapter(getSupportFragmentManager());
            for(int i= 0; i < list.result.size(); i++){
                fragment1 = new MovieListFragment();
                fragments.add(i, fragment1);
                MovieInfo movieinfo = list.result.get(i);

                movieData.add(i, new MovieData(movieinfo.id, movieinfo.title, movieinfo.reservation_rate, movieinfo.grade, movieinfo.image));
                putMovieDataToFragment(fragment1, movieData.get(i));

                DatabaseManager.insertMovieData(getApplicationContext(), tableName, movieinfo.id, movieinfo.title, movieinfo.reservation_rate,
                        movieinfo.grade, movieinfo.image, movieinfo.date, movieinfo.user_rating);

                adapter.addItem(fragment1);
            }

            pager.setAdapter(adapter);

        }
    }

    public void putMovieDataToFragment(Fragment fragment,  MovieData movieData){
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie_data", movieData);
        fragment.setArguments(bundle);
    }

    class MoviePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> items = new ArrayList<Fragment>();

        public MoviePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item) {
            items.add(item);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "페이지 " + position;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu, menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_0) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem Item = menu.findItem(R.id.order);
        LinearLayout rootView = (LinearLayout) Item.getActionView();

        orderButton = rootView.findViewById(R.id.orderButton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(Item);
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order:
                if (isShown) {
                    menuContainer.startAnimation(translateUp);
                } else {
                    menuContainer.setVisibility(View.VISIBLE);
                    menuContainer.startAnimation(translateDown);

                    Button button2 = menuContainer.findViewById(R.id.button2);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            menuContainer.startAnimation(translateUp);
                            orderButton.setBackgroundResource(R.drawable.order11);
                            sortMovieListData(typeOne, "reservation_rate");
                        }
                    });

                    Button button3 = menuContainer.findViewById(R.id.button3);
                    button3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            menuContainer.startAnimation(translateUp);
                            orderButton.setBackgroundResource(R.drawable.order22);
                            sortMovieListData(typeTwo, "rating");
                        }
                    });

                    Button button4 = menuContainer.findViewById(R.id.button4);
                    button4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            menuContainer.startAnimation(translateUp);
                            orderButton.setBackgroundResource(R.drawable.order33);
                            sortMovieListData(typeThree, "date");
                        }
                    });
                }

                isShown = !isShown;
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void sortMovieListData(int type, String data) {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            if (AppHelper.requestQueue == null) {
                AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            requestMovieList(type);
        }
        else {

            ArrayList<MovieData> newData = DatabaseManager.SortMovieData(getApplicationContext(), tableName, data);
            if (newData != null) {
                Toast.makeText(getApplicationContext(), "인터넷 연결 실패 : 테이블에 저장된 데이터를 불러옵니다.", Toast.LENGTH_LONG).show();

                MovieListFragment fragment;
                MoviePagerAdapter newAdapter = new MoviePagerAdapter(getSupportFragmentManager());

                for (int i = 0; i < newData.size(); i++) {
                    fragment = MovieListFragment.newInstance(newData.get(i));
                    newAdapter.addItem(fragment);
                }
                pager.setAdapter(newAdapter);

            } else {
                Toast.makeText(getApplicationContext(), "테이블 조회 실패 : 인터넷을 연결해주세요.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onTitleChange(String title) {
        toolbar.setTitle(title);
    }

}
