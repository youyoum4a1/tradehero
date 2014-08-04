package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import com.squareup.picasso.LruCache;
import com.tradehero.common.utils.SlowedAsyncTask;
import com.tradehero.th.R;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.dagger.ForPicasso;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ClearCacheViewHolder extends OneSettingViewHolder
{
    @NotNull private final ProgressDialogUtil progressDialogUtil;
    @NotNull private final LruCache lruCache;
    private ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    @Inject public ClearCacheViewHolder(
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull @ForPicasso LruCache lruCache)
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
        if (progressDialog == null)
        {
            progressDialog = progressDialogUtil.show(preferenceFragment.getActivity(),
                    R.string.settings_misc_cache_clearing_alert_title,
                    R.string.settings_misc_cache_clearing_alert_message);
        }
        else
        {
            progressDialog.setTitle(R.string.settings_misc_cache_clearing_alert_title);
            progressDialog.setMessage(preferenceFragment.getString(R.string.settings_misc_cache_clearing_alert_message));
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
        FragmentActivity activity = preferenceFragment.getActivity();
        if (activity != null)
        {
            if (progressDialog == null)
            {
                progressDialog = progressDialogUtil.show(preferenceFragment.getActivity(),
                        R.string.settings_misc_cache_cleared_alert_title,
                        R.string.empty);
            }
            else
            {
                progressDialog.setTitle(R.string.settings_misc_cache_cleared_alert_title);
                progressDialog.setMessage("");
                progressDialog.show();
            }
            preferenceFragment.getView().postDelayed(new Runnable()
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
