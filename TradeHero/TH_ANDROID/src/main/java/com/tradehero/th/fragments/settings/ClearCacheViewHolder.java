package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.preference.PreferenceFragment;
import android.util.Pair;
import com.squareup.picasso.LruCache;
import com.tradehero.common.rx.DurationMeasurer;
import com.tradehero.common.rx.MinimumApparentDelayer;
import com.tradehero.th.R;
import com.tradehero.th.utils.dagger.ForPicasso;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ClearCacheViewHolder extends OneSettingViewHolder
{
    private static final Pair<Long, TimeUnit> APPARENT_DURATION = Pair.create(500l, TimeUnit.MILLISECONDS);

    @NonNull private final LruCache lruCache;

    //<editor-fold desc="Constructors">
    @Inject public ClearCacheViewHolder(
            @NonNull @ForPicasso LruCache lruCache)
    {
        this.lruCache = lruCache;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_clear_cache;
    }

    @Override protected void handlePrefClicked()
    {
        ProgressDialog progressDialog = null;
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            Context activityContext = preferenceFragmentCopy.getActivity();
            if (activityContext != null)
            {
                progressDialog = ProgressDialog.show(
                        activityContext,
                        activityContext.getString(R.string.settings_misc_cache_clearing_alert_title),
                        activityContext.getString(R.string.settings_misc_cache_clearing_alert_message),
                        true);
            }
        }

        final ProgressDialog finalProgressDialog = progressDialog;
        new DurationMeasurer<>(
                new Action1<Integer>()
                {
                    @Override public void call(Integer integer)
                    {
                        ClearCacheViewHolder.this.flushCache();
                    }
                },
                APPARENT_DURATION.second,
                Schedulers.computation())
                .call(1)
                .flatMap(new MinimumApparentDelayer<>(1, APPARENT_DURATION))
                .doOnNext(ignored -> showCacheCleared(finalProgressDialog))
                .delay(APPARENT_DURATION.first, APPARENT_DURATION.second, AndroidSchedulers.mainThread())
                .subscribe(
                        ignored -> {
                            if (finalProgressDialog != null)
                            {
                                finalProgressDialog.dismiss();
                            }
                        },
                        e -> Timber.e(e, "Failed to clear cache"));
    }

    private void flushCache()
    {
        lruCache.clear();
    }

    private void showCacheCleared(@Nullable ProgressDialog progressDialog)
    {
        if (progressDialog != null)
        {
            progressDialog.setTitle(R.string.settings_misc_cache_cleared_alert_title);
            progressDialog.setMessage("");
        }
    }
}
