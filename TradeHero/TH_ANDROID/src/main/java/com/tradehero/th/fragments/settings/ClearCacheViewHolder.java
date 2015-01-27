package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.preference.PreferenceFragment;
import android.util.Pair;
import com.squareup.picasso.LruCache;
import com.tradehero.common.rx.MinimumApparentDelayer;
import com.tradehero.common.rx.DurationMeasurer;
import com.tradehero.th.R;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.dagger.ForPicasso;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ClearCacheViewHolder extends OneSettingViewHolder
{
    private static final Pair<Long, TimeUnit> APPARENT_DURATION = Pair.create(500l, TimeUnit.MILLISECONDS);

    @NonNull private final ProgressDialogUtil progressDialogUtil;
    @NonNull private final LruCache lruCache;
    @Nullable private ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    @Inject public ClearCacheViewHolder(
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull @ForPicasso LruCache lruCache)
    {
        this.progressDialogUtil = progressDialogUtil;
        this.lruCache = lruCache;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_clear_cache;
    }

    @Override protected void handlePrefClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (progressDialog == null)
        {
            if (preferenceFragmentCopy != null)
            {
                Context activityContext = preferenceFragmentCopy.getActivity();
                if (activityContext != null)
                {
                    progressDialog = progressDialogUtil.show(
                            activityContext,
                            R.string.settings_misc_cache_clearing_alert_title,
                            R.string.settings_misc_cache_clearing_alert_message);
                }
            }
        }
        else
        {
            progressDialog.setTitle(R.string.settings_misc_cache_clearing_alert_title);
            if (preferenceFragmentCopy != null)
            {
                progressDialog.setMessage(preferenceFragmentCopy.getString(R.string.settings_misc_cache_clearing_alert_message));
            }
            progressDialog.show();
        }

        Observable.just(1)
                .flatMap(new DurationMeasurer<>(
                        integer -> flushCache(),
                        APPARENT_DURATION.second,
                        Schedulers.computation()))
                .flatMap(new MinimumApparentDelayer<>(1, APPARENT_DURATION))
                .doOnNext(ignored -> showCacheCleared())
                .delay(APPARENT_DURATION.first, APPARENT_DURATION.second, AndroidSchedulers.mainThread())
                .subscribe(
                        ignored -> dismissProgress(),
                        e -> Timber.e(e, "Failed to clear cache"));
    }

    private void flushCache()
    {
        lruCache.clear();
    }

    private void showCacheCleared()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            FragmentActivity activity = preferenceFragment.getActivity();
            if (activity != null)
            {
                if (progressDialog == null)
                {
                    progressDialog = progressDialogUtil.show(
                            activity,
                            R.string.settings_misc_cache_cleared_alert_title,
                            R.string.empty);
                }
                else
                {
                    progressDialog.setTitle(R.string.settings_misc_cache_cleared_alert_title);
                    progressDialog.setMessage("");
                    progressDialog.show();
                }
            }
        }
    }

    private void dismissProgress()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.hide();
        }
    }
}
