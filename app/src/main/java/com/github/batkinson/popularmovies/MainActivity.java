package com.github.batkinson.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String URI_KEY = "uri_key";
    private static final String API_PARAM_KEY = "api_key";

    private static final int IMAGE_WIDTH = 185;
    private static final String POPULAR_URI = "http://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_URI = "http://api.themoviedb.org/3/movie/top_rated";
    private static final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p/w" + IMAGE_WIDTH;

    private MovieListAdapter adapter;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private String uri;

    MenuItem popularItem, topRatedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            uri = POPULAR_URI;
        } else {
            uri = savedInstanceState.getString(URI_KEY, POPULAR_URI);
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        ImageCache imageCache = new ImageCache();
        imageLoader = new ImageLoader(requestQueue, imageCache);

        GridView movieList = (GridView) findViewById(R.id.movie_list);
        movieList.setColumnWidth(IMAGE_WIDTH);

        adapter = new MovieListAdapter(this, -1);
        movieList.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(URI_KEY, uri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        popularItem = menu.findItem(R.id.show_popular);
        topRatedItem = menu.findItem(R.id.show_top_rated);
        updateUriMenuItems(uri);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_popular:
                selectUri(POPULAR_URI);
                return true;
            case R.id.show_top_rated:
                selectUri(TOP_RATED_URI);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectUri(String uri) {
        this.uri = uri;
        updateUriMenuItems(uri);
        fetchMovies();
    }

    private void updateUriMenuItems(String uri) {
        if (popularItem != null) {
            popularItem.setChecked(POPULAR_URI.equals(uri));
        }
        if (topRatedItem != null) {
            topRatedItem.setChecked(TOP_RATED_URI.equals(uri));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectUri(uri);
    }

    private void fetchMovies() {
        requestQueue.add(new JsonObjectRequest(getApiUrl(uri), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    adapter.setContents(response);
                } catch (JSONException e) {
                    Log.w(TAG, "failed to load movies", e);
                }
            }
        }, null));
    }

    private String getApiUrl(String baseUrl) {
        return Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_PARAM_KEY, BuildConfig.MOVIE_DB_API_KEY).build().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestQueue.stop();
    }

    private class MovieListAdapter extends ArrayAdapter<JSONObject> {

        public static final String POSTER_PATH = "poster_path";
        public static final String RESULTS = "results";

        public MovieListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            NetworkImageView imageView;
            if (convertView == null) {
                imageView = new NetworkImageView(getContext());
            } else {
                imageView = (NetworkImageView) convertView;
            }

            JSONObject movie = getItem(position);
            try {
                String imageUri = Uri.parse(IMAGE_BASE_URI).buildUpon()
                        .appendEncodedPath(movie.getString(POSTER_PATH)).build().toString();
                imageView.setImageUrl(imageUri, imageLoader);
            } catch (JSONException e) {
                Log.e(TAG, "failed to construct image url", e);
            }

            return imageView;
        }

        public void setContents(JSONObject resultRoot) throws JSONException {
            JSONArray results = resultRoot.getJSONArray(RESULTS);
            try {
                setNotifyOnChange(false);
                clear();
                for (int i = 0; i < results.length(); i++) {
                    add(results.getJSONObject(i));
                }
            } finally {
                notifyDataSetChanged();
                setNotifyOnChange(true);
            }
        }
    }
}
