package com.github.batkinson.popularmovies;

import android.content.Context;
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
import com.github.batkinson.popularmovies.databinding.ViewReviewBinding;
import com.github.batkinson.popularmovies.databinding.ViewTrailerBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.databinding.DataBindingUtil.inflate;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.batkinson.popularmovies.Api.IMAGE_HEIGHT;
import static com.github.batkinson.popularmovies.Api.IMAGE_WIDTH;
import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.Api.RESULTS;
import static com.github.batkinson.popularmovies.Api.getReviewsPath;
import static com.github.batkinson.popularmovies.Api.getVideosUri;
import static com.github.batkinson.popularmovies.DetailFragment.TAG;
import static com.github.batkinson.popularmovies.R.layout.fragment_detail;
import static com.github.batkinson.popularmovies.R.layout.view_review;
import static com.github.batkinson.popularmovies.R.layout.view_trailer;

public class DetailFragment extends Fragment {

    static final String TAG = DetailActivity.class.getSimpleName();

    private VolleyService volley;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volley = VolleyService.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDetailBinding binding = inflate(inflater, fragment_detail, container, false);

        Bundle args = getArguments();

        binding.thumbnail.setMinimumWidth(IMAGE_WIDTH);
        binding.thumbnail.setMinimumHeight(IMAGE_HEIGHT);
        try {
            MovieDetail detail = new MovieDetail(new JSONObject(args.getString(MOVIE_KEY)));
            binding.setMovie(detail);
            binding.thumbnail.setImageUrl(detail.getImageUrl(), volley.getImageLoader());
            RequestQueue queue = volley.getRequestQueue();
            queue.add(new JsonObjectRequest(getVideosUri(detail.getId()), null, new TrailerHandler(getContext(), binding.trailers), null));
            queue.add(new JsonObjectRequest(getReviewsPath(detail.getId()), null, new ReviewHandler(getContext(), binding.reviews), null));
        } catch (JSONException e) {
            Log.e(TAG, "failed populating details", e);
        }

        return binding.getRoot();
    }
}


abstract class ResponseHandler<T> implements Response.Listener<T> {

    Context ctx;
    LayoutInflater inflater;
    ViewGroup container;
    UriLauncher launcher;

    ResponseHandler(Context ctx, ViewGroup container) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.container = container;
        launcher = new UriLauncher(ctx);
    }
}


class TrailerHandler extends ResponseHandler<JSONObject> {

    TrailerHandler(Context ctx, ViewGroup container) {
        super(ctx, container);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray results = response.getJSONArray(RESULTS);
            for (int i = 0; i < results.length(); i++) {
                MovieVideo video = new MovieVideo(results.getJSONObject(i));
                if (video.isTrailer()) {
                    ViewTrailerBinding binding = inflate(inflater, view_trailer, container, false);
                    binding.setVideo(video);
                    binding.setLauncher(launcher);
                    container.addView(binding.getRoot());
                }
            }
            container.setVisibility(results.length() > 0 ? VISIBLE : GONE);
        } catch (JSONException e) {
            Log.w(TAG, "failed to populate trailers from json", e);
        }
    }
}


class ReviewHandler extends ResponseHandler<JSONObject> {

    ReviewHandler(Context ctx, ViewGroup container) {
        super(ctx, container);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONArray results = response.getJSONArray(RESULTS);
            boolean showTrailers = false;
            for (int i = 0; i < results.length(); i++) {
                ViewReviewBinding binding = inflate(inflater, view_review, container, false);
                binding.setReview(new MovieReview(results.getJSONObject(i)));
                binding.setLauncher(launcher);
                container.addView(binding.getRoot());
                showTrailers = true;
            }
            container.setVisibility(showTrailers ? VISIBLE : GONE);
        } catch (JSONException e) {
            Log.w(TAG, "failed to populate reviews from json", e);
        }
    }
}
