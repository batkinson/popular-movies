package com.github.batkinson.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.github.batkinson.popularmovies.databinding.FragmentPosterBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.github.batkinson.popularmovies.Api.IMAGE_WIDTH;
import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.Api.POPULAR_URI;
import static com.github.batkinson.popularmovies.Api.POSTER_PATH;
import static com.github.batkinson.popularmovies.Api.RESULTS;
import static com.github.batkinson.popularmovies.Api.TOP_RATED_URI;
import static com.github.batkinson.popularmovies.Api.URI_KEY;
import static com.github.batkinson.popularmovies.Api.getApiUri;
import static com.github.batkinson.popularmovies.Api.getPosterUri;

public class PosterFragment extends Fragment {

    private static final String TAG = PosterFragment.class.getSimpleName();

    private MovieListAdapter adapter;
    private String uri;
    private MenuItem popularItem, topRatedItem;

    private VolleyService volley;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            uri = POPULAR_URI;
        } else {
            uri = savedInstanceState.getString(URI_KEY, POPULAR_URI);
        }

        volley = VolleyService.getInstance(getContext());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentPosterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_poster, container, false);

        Context ctx = getContext();
        adapter = new MovieListAdapter(ctx, -1);

        binding.movieList.setColumnWidth(IMAGE_WIDTH);
        binding.movieList.setOnItemClickListener(new PosterClickHandler(ctx));
        binding.movieList.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(URI_KEY, uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        popularItem = menu.findItem(R.id.show_popular);
        topRatedItem = menu.findItem(R.id.show_top_rated);
        updateMenuSelection(uri);
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
        updateMenuSelection(uri);
        fetchMovies();
    }

    private void updateMenuSelection(String uri) {
        if (popularItem != null) {
            popularItem.setChecked(POPULAR_URI.equals(uri));
        }
        if (topRatedItem != null) {
            topRatedItem.setChecked(TOP_RATED_URI.equals(uri));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        selectUri(uri);
    }

    private void fetchMovies() {
        volley.getRequestQueue().add(new JsonObjectRequest(getApiUri(uri), null, new Response.Listener<JSONObject>() {
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

    private static class PosterClickHandler implements AdapterView.OnItemClickListener {

        private Context ctx;

        public PosterClickHandler(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Object tag = view.getTag();
            if (tag instanceof JSONObject) {
                JSONObject movie = ((JSONObject) tag);
                launchDetail(movie.toString());
            }
        }

        private void launchDetail(String movie) {
            Intent detailIntent = new Intent();
            detailIntent.setClass(ctx, DetailActivity.class);
            detailIntent.putExtra(MOVIE_KEY, movie);
            ctx.startActivity(detailIntent);
        }

    }

    private class MovieListAdapter extends ArrayAdapter<JSONObject> {

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
            imageView.setTag(movie);

            try {
                String posterPath = movie.getString(POSTER_PATH);
                String imageUri = getPosterUri(posterPath);
                imageView.setImageUrl(imageUri, volley.getImageLoader());
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
