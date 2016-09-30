package com.github.batkinson.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.batkinson.popularmovies.databinding.FragmentDetailBinding;

import org.json.JSONException;
import org.json.JSONObject;

import static com.github.batkinson.popularmovies.Api.IMAGE_HEIGHT;
import static com.github.batkinson.popularmovies.Api.IMAGE_WIDTH;
import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.Api.getReviewsPath;
import static com.github.batkinson.popularmovies.Api.getVideosUri;
import static com.github.batkinson.popularmovies.R.layout.fragment_detail;

public class DetailFragment extends Fragment {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private VolleyService volley;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Context ctx = getContext();

        volley = VolleyService.getInstance(ctx);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDetailBinding binding = DataBindingUtil.inflate(inflater, fragment_detail, container, false);

        Bundle args = getArguments();

        binding.thumbnail.setMinimumWidth(IMAGE_WIDTH);
        binding.thumbnail.setMinimumHeight(IMAGE_HEIGHT);
        try {
            MovieDetail detail = new MovieDetail(new JSONObject(args.getString(MOVIE_KEY)));
            binding.setMovie(detail);
            binding.thumbnail.setImageUrl(detail.getImageUrl(), volley.getImageLoader());
            RequestQueue queue = volley.getRequestQueue();
            RequestLogger logger = new RequestLogger();
            queue.add(new JsonObjectRequest(getVideosUri(detail.getId()), null, logger, null));
            queue.add(new JsonObjectRequest(getReviewsPath(detail.getId()), null, logger, null));
        } catch (JSONException e) {
            Log.e(TAG, "failed populating details", e);
        }


        return binding.getRoot();
    }

    private static class RequestLogger implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject response) {
            Log.i(TAG, response.toString());
        }
    }
}
