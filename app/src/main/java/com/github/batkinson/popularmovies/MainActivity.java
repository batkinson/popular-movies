package com.github.batkinson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONObject;

import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;

public class MainActivity extends AppCompatActivity implements PosterFragment.PosterSelectionHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onPosterSelected(JSONObject movie) {
        Intent detailIntent = new Intent();
        detailIntent.setClass(this, DetailActivity.class);
        detailIntent.putExtra(MOVIE_KEY, movie.toString());
        startActivity(detailIntent);
    }
}