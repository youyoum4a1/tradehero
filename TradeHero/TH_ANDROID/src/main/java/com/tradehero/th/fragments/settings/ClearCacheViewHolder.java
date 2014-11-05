package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.preference.PreferenceFragment;
import com.squareup.picasso.LruCache;
import com.tradehero.common.utils.SlowedAsyncTask;
import com.tradehero.th.R;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.dagger.ForPicasso;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ClearCacheViewHolder extends OneSettingViewHolder
{
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

        new SlowedAsyncTask<Void, Void, Void>(500)
        {
            @Override protected Void doBackgroundAction(Void... voids)
            {
                flushCache();
                return null;
            }

            @Override protected void onPostExecute(Void aVoid)
            {
                handleCacheCleared();
            }
        }.execute();

    }

    private void flushCache()
    {
        lruCache.clear();
    }

    private void handleCacheCleared()
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
                preferenceFragmentCopy.getView().postDelayed(new Runnable()
                {
                    @Override public void run()
                    {
                        ProgressDialog progressDialogCopy = progressDialog;
                        if (progressDialogCopy != null)
                        {
                            progressDialogCopy.hide();
                        }
                    }
                }, 500);
            }
        }
    }
}
