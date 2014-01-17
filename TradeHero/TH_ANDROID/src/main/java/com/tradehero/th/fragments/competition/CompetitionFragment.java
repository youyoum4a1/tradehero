package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.competition.ProviderCache;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
abstract public class CompetitionFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = CompetitionFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_PROVIDER_ID = CompetitionFragment.class.getName() + ".providerId";

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Inject protected ProviderCache providerCache;
    private DTOCache.Listener<ProviderId, ProviderDTO> providerCacheListener;
    private DTOCache.GetOrFetchTask<ProviderId, ProviderDTO> providerCacheFetchTask;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_PROVIDER_ID))
        {
            this.providerId = new ProviderId(savedInstanceState.getBundle(BUNDLE_KEY_PROVIDER_ID));
        }
        else if (getArguments() != null && getArguments().containsKey(BUNDLE_KEY_PROVIDER_ID))
        {
            this.providerId = new ProviderId(getArguments().getBundle(BUNDLE_KEY_PROVIDER_ID));
        }
        else
        {
            throw new IllegalArgumentException("There is no defined providerId");
        }

        this.providerCacheListener = new CompetitionFragmentProviderCacheListener();
    }

    @Override public void onStart()
    {
        super.onStart();
        this.detachProviderFetchTask();
        this.providerCacheFetchTask = providerCache.getOrFetch(this.providerId, this.providerCacheListener);
        this.providerCacheFetchTask.execute();
    }

    @Override public void onStop()
    {
        this.detachProviderFetchTask();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.providerCacheListener = null;
        super.onDestroy();
    }

    protected void detachProviderFetchTask()
    {
        if (this.providerCacheFetchTask != null)
        {
            this.providerCacheFetchTask.setListener(null);
        }
        this.providerCacheFetchTask = null;
    }

    protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        if (andDisplay)
        {

        }
    }

    protected class CompetitionFragmentProviderCacheListener implements DTOCache.Listener<ProviderId, ProviderDTO>
    {
        @Override public void onDTOReceived(ProviderId key, ProviderDTO value)
        {
            if (key.equals(CompetitionFragment.this.providerId))
            {
                CompetitionFragment.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(ProviderId key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_info));
            THLog.e(TAG, "Error fetching the provider info " + key, error);
        }
    }
}
