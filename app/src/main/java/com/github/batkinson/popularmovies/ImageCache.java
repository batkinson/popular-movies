package com.github.batkinson.popularmovies;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

class ImageCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {

    private static final int DEFAULT_SIZE_BYTES = 1024 * 1024 * 25;

    ImageCache() {
        this(DEFAULT_SIZE_BYTES);
    }

    private ImageCache(int maxSize) {
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

}
