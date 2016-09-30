package com.github.batkinson.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.batkinson.popularmovies.databinding.FragmentDetailBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Intent.ACTION_VIEW;
import static com.github.batkinson.popularmovies.Api.IMAGE_HEIGHT;
import static com.github.batkinson.popularmovies.Api.IMAGE_WIDTH;
import static com.github.batkinson.popularmovies.Api.MOVIE_KEY;
import static com.github.batkinson.popularmovies.Api.getReviewsPath;
import static com.github.batkinson.popularmovies.Api.getVideosUri;
import static com.github.batkinson.popularmovies.Api.getYouTubeUri;
import static com.github.batkinson.popularmovies.R.layout.fragment_detail;
import static com.github.batkinson.popularmovies.R.layout.view_trailer;

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
            queue.add(new JsonObjectRequest(getVideosUri(detail.getId()), null, new TrailerPopulator(getContext(), binding.trailerList), null));
            queue.add(new JsonObjectRequest(getReviewsPath(detail.getId()), null, new RequestLogger(), null));
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

    private static class TrailerPopulator implements Response.Listener<JSONObject> {

        Context ctx;
        LayoutInflater inflater;
        ViewGroup container;

        TrailerPopulator(Context ctx, ViewGroup container) {
            this.ctx = ctx;
            inflater = LayoutInflater.from(ctx);
            this.container = container;
        }

        @Override
        public void onResponse(JSONObject response) {
            try {
                JSONArray results = response.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    final String key = result.getString("key");
                    if ("trailer".equalsIgnoreCase(result.getString("type"))) {
                        TextView trailer = (TextView) inflater.inflate(view_trailer, null);
                        trailer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ctx.startActivity(new Intent(ACTION_VIEW, getYouTubeUri(key)));
                            }
                        });
                        trailer.setText(result.getString("name"));
                        container.addView(trailer);
                    }
                }
            } catch (JSONException e) {
                Log.w(TAG, "failed to populate trailers from json", e);
            }
        }
    }
}
