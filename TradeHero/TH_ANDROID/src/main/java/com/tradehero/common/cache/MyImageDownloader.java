package com.tradehero.common.cache;

import android.content.Context;
import android.net.Uri;
import com.squareup.picasso.UrlConnectionDownloader;
import java.io.IOException;


public class MyImageDownloader extends UrlConnectionDownloader {

    private Context context;
    public MyImageDownloader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Response load(Uri uri, boolean localCacheOnly) throws IOException {
        //LruMemFileCache cache = LruMemFileCache.getInstance(context.getApplicationContext());
        //cache.getAsync()
        //we cannot get key or Request params.
        return super.load(uri,localCacheOnly);
    }
}
