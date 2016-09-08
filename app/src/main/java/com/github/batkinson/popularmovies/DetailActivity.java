package com.github.batkinson.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.batkinson.popularmovies.databinding.ActivityDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

import static com.github.batkinson.popularmovies.Api.IMAGE_HEIGHT;
import static com.github.batkinson.popularmovies.Api.IMAGE_WIDTH;
import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private VolleyService volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volley = VolleyService.getInstance(this);

        ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.thumbnail.setMinimumWidth(IMAGE_WIDTH);
        binding.thumbnail.setMinimumHeight(IMAGE_HEIGHT);
        try {
            MovieDetail detail = new MovieDetail(new JSONObject(getIntent().getStringExtra(MOVIE_KEY)));
            binding.setMovie(detail);
            binding.thumbnail.setImageUrl(detail.getImageUrl(), volley.getImageLoader());
        } catch (JSONException e) {
            Log.e(TAG, "failed populating details", e);
        }
    }
}

