package com.github.batkinson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.json.JSONObject;

import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.R.id.detail_frame;
import static com.github.batkinson.popularmovies.R.layout.activity_main;

public class MainActivity extends AppCompatActivity implements PosterFragment.PosterSelectionHandler {

    private static final String DETAIL_FRAG_TAG = "DETAIL_FRAG";
    private boolean dualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        View detailContainer = findViewById(detail_frame);
        dualPane = detailContainer != null;
    }

    @Override
    public void onPosterSelected(JSONObject movie) {
        if (dualPane) {
            updateDetail(movie);
        } else {
            launchDetail(movie);
        }
    }

    private void updateDetail(JSONObject movie) {
        Bundle args = new Bundle();
        args.putString(MOVIE_KEY, movie.toString());
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(detail_frame, detailFragment, DETAIL_FRAG_TAG)
                .commit();
    }

    private void launchDetail(JSONObject movie) {
        Intent detailIntent = new Intent();
        detailIntent.setClass(this, DetailActivity.class);
        detailIntent.putExtra(MOVIE_KEY, movie.toString());
        startActivity(detailIntent);
    }
}