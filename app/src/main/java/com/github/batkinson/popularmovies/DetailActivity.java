package com.github.batkinson.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.Api.ORIGINAL_TITLE;
import static com.github.batkinson.popularmovies.Api.OVERVIEW;
import static com.github.batkinson.popularmovies.Api.POSTER_PATH;
import static com.github.batkinson.popularmovies.Api.RELEASE_DATE;
import static com.github.batkinson.popularmovies.Api.VOTE_AVERAGE;
import static com.github.batkinson.popularmovies.Api.getPosterUri;
import static com.github.batkinson.popularmovies.Api.getReleaseYear;
import static java.lang.String.format;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private VolleyService volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        volley = VolleyService.getInstance(this);
        try {
            populate();
        } catch (JSONException e) {
            Log.e(TAG, "failed populating details", e);
        }
    }

    private void populate() throws JSONException {

        JSONObject movie = new JSONObject(getIntent().getStringExtra(MOVIE_KEY));

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.thumbnail);
        imageView.setImageUrl(getPosterUri(movie.getString(POSTER_PATH)), volley.getImageLoader());

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(movie.getString(ORIGINAL_TITLE));

        TextView releaseView = (TextView) findViewById(R.id.release_date);
        try {
            releaseView.setText(format(getString(R.string.release_date), getReleaseYear(movie.getString(RELEASE_DATE))));
        } catch (ParseException e) {
            releaseView.setText(R.string.unknown_release_date);
        }

        TextView ratingView = (TextView) findViewById(R.id.rating);
        ratingView.setText(format(getString(R.string.rating), movie.getString(VOTE_AVERAGE)));

        TextView overviewView = (TextView) findViewById(R.id.overview);
        overviewView.setText(movie.getString(OVERVIEW));
    }
}
