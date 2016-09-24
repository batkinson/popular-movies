package com.github.batkinson.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.R.id.detail;
import static com.github.batkinson.popularmovies.R.layout.activity_detail;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_detail);

        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(MOVIE_KEY, getIntent().getStringExtra(MOVIE_KEY));
        detailFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .add(detail, detailFragment)
                .commit();
    }

}

