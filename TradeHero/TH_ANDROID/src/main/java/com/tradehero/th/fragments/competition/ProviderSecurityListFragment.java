package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderSecurityListType;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.trending.SecurityItemViewAdapter;
import com.tradehero.th.persistence.competition.ProviderCache;
import javax.inject.Inject;

/**
 * Created by xavier on 1/21/14.
 */
public class ProviderSecurityListFragment extends SecurityListFragment
{
    public static final String TAG = ProviderSecurityListFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_PROVIDER_ID = ProviderSecurityListFragment.class.getName() + ".providerId";
    public final static int SECURITY_ID_LIST_LOADER_ID = 2531;

    // TODO sort warrants
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Inject protected ProviderCache providerCache;
    private DTOCache.Listener<ProviderId, ProviderDTO> providerCacheListener;
    private DTOCache.GetOrFetchTask<ProviderId, ProviderDTO> providerCacheFetchTask;

    @Inject SecurityItemLayoutFactory securityItemLayoutFactory;

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

        this.providerCacheListener = new ProviderSecurityListFragmentProviderCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //THLog.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_provider_security_list, container, false);
        initViews(view);
        return view;
    }

    @Override public void onStart()
    {
        THLog.d(TAG, "onStart");
        super.onStart();
        this.detachProviderFetchTask();
        this.providerCacheFetchTask = providerCache.getOrFetch(this.providerId, this.providerCacheListener);
        this.providerCacheFetchTask.execute();
        forceInitialLoad();
    }

    @Override public void onStop()
    {
        THLog.d(TAG, "onStop");
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

    @Override protected SecurityItemViewAdapter createSecurityItemViewAdapter()
    {
        return new SecurityItemViewAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                securityItemLayoutFactory.getProviderLayout(providerId));
    }

    @Override public int getSecurityIdListLoaderId()
    {
        return SECURITY_ID_LIST_LOADER_ID;
    }

    @Override public ProviderSecurityListType getSecurityListType(int page)
    {
        return new BasicProviderSecurityListType(providerId, page, perPage);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected class ProviderSecurityListFragmentProviderCacheListener implements DTOCache.Listener<ProviderId, ProviderDTO>
    {
        @Override public void onDTOReceived(ProviderId key, ProviderDTO value)
        {
            if (key.equals(ProviderSecurityListFragment.this.providerId))
            {
                ProviderSecurityListFragment.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(ProviderId key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_info));
            THLog.e(TAG, "Error fetching the provider info " + key, error);
        }
    }
}
