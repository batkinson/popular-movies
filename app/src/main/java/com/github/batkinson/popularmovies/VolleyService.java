package com.github.batkinson.popularmovies;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleyService {

    public static VolleyService INSTANCE;

    private Context ctx;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    protected VolleyService(Context ctx) {
        this.ctx = ctx.getApplicationContext();
        requestQueue = Volley.newRequestQueue(this.ctx);
        ImageCache imageCache = new ImageCache();
        imageLoader = new ImageLoader(requestQueue, imageCache);
    }

    public static VolleyService getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new VolleyService(ctx);
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
