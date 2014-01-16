package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.HelpVideoListKey;
import com.tradehero.th.persistence.competition.HelpVideoListCache;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
public class ProviderVideoListFragment extends CompetitionFragment
{
    public static final String TAG = ProviderVideoListFragment.class.getSimpleName();

    @Inject protected HelpVideoListCache helpVideoListCache;
    private DTOCache.Listener<HelpVideoListKey, HelpVideoIdList> helpVideoListCacheListener;
    private DTOCache.GetOrFetchTask<HelpVideoListKey, HelpVideoIdList> helpVideoListFetchTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.helpVideoListCacheListener = new ProviderVideoListFragmentVideoListCacheListener();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();

        detachListVideoFetchTask();
        this.helpVideoListFetchTask = this.helpVideoListCache.getOrFetch(new HelpVideoListKey(this.providerId), this.helpVideoListCacheListener);
        this.helpVideoListFetchTask.execute();
    }

    @Override public void onDestroyView()
    {
        detachListVideoFetchTask();
        this.helpVideoListCacheListener = null;
        super.onDestroyView();
    }

    private void detachListVideoFetchTask()
    {
        if (this.helpVideoListFetchTask != null)
        {
            this.helpVideoListFetchTask.setListener(null);
        }
        this.helpVideoListFetchTask = null;
    }



    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    private class ProviderVideoListFragmentVideoListCacheListener implements DTOCache.Listener<HelpVideoListKey, HelpVideoIdList>
    {
        @Override public void onDTOReceived(HelpVideoListKey key, HelpVideoIdList value)
        {
            // TODO
        }

        @Override public void onErrorThrown(HelpVideoListKey key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_help_video_list_info));
            THLog.e(TAG, "Error fetching the list of help videos " + key, error);
        }
    }
}
