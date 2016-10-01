package com.github.batkinson.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import static android.content.Intent.ACTION_VIEW;

public class UriLauncher {

    private Context ctx;

    public UriLauncher(Context ctx) {
        this.ctx = ctx;
    }

    public void launch(Uri uriToView) {
        ctx.startActivity(new Intent(ACTION_VIEW, uriToView));
    }
}