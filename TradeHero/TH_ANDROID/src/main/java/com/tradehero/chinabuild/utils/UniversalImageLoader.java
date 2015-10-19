package com.tradehero.chinabuild.utils;

import android.content.Context;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.tradehero.th.R;

import java.io.File;

/**
 * Created by palmer on 15/3/27.
 */
public class UniversalImageLoader {


    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(6 * 1024 * 1024))
                .memoryCacheSize(6 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(300)
                .discCache(new UnlimitedDiscCache(new File(StorageUtils.getImageStoragePath(context))))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000))
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getAvatarImageLoaderOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.avatar_default)
                .showImageForEmptyUri(R.drawable.avatar_default)
                .showImageOnFail(R.drawable.avatar_default)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return options;
    }

    public static DisplayImageOptions getAdvertisementImageLoaderOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return options;
    }

    public static DisplayImageOptions getVideoImageLoaderOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_default)
                .showImageForEmptyUri(R.drawable.bg_default)
                .showImageOnFail(R.drawable.bg_default)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return options;
    }

    public static DisplayImageOptions getAvatarImageLoaderOptions(boolean enableDiskCache){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.avatar_default)
                .showImageForEmptyUri(R.drawable.avatar_default)
                .showImageOnFail(R.drawable.avatar_default)
                .cacheInMemory(true)
                .cacheOnDisc(enableDiskCache)
                .build();
        return options;
    }

    public static DisplayImageOptions getDisplayLargeImageOptions(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        return options;
    }
}
