package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.DeviceUtil;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class ProviderSecurityListFragment extends SecurityListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = ProviderSecurityListFragment.class.getName() + ".providerId";
    public final static int SECURITY_ID_LIST_LOADER_ID = 2531;

    // TODO sort warrants
    @NotNull protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Inject ProviderCache providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject SecurityItemViewAdapterFactory securityItemViewAdapterFactory;

    private DTOCacheNew.Listener<ProviderId, ProviderDTO> providerCacheListener;

    public static void putProviderId(@NotNull Bundle args, @NotNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NotNull private static ProviderId getProviderId(@NotNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_PROVIDER_ID))
        {
            this.providerId = getProviderId(savedInstanceState);
        }
        else
        {
            this.providerId = getProviderId(getArguments());
        }
        this.providerCacheListener = createProviderCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_provider_security_list, container, false);
        initViews(view);
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProviderDTO();
    }

    @Override public void onResume()
    {
        super.onResume();
        forceInitialLoad();
    }

    @Override public void onStop()
    {
        this.detachProviderFetchTask();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        DeviceUtil.dismissKeyboard(getActivity());
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.providerCacheListener = null;
        super.onDestroy();
    }

    protected void detachProviderFetchTask()
    {
        providerCache.unregister(providerCacheListener);
    }

    protected void fetchProviderDTO()
    {
        this.detachProviderFetchTask();
        providerCache.register(this.providerId, providerCacheListener);
        providerCache.getOrFetchAsync(this.providerId);
        //forceInitialLoad();
    }

    protected void prepareSecurityLoader()
    {
        getActivity().getSupportLoaderManager().initLoader(getSecurityIdListLoaderId(), null, new ProviderSecurityListLoaderCallback());
    }

    protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;

        getActivity().invalidateOptionsMenu();

        if (andDisplay)
        {
            displayTitle();
        }
    }

    protected void displayTitle()
    {
        if (providerDTO != null
                && providerDTO.specificResources != null
                && providerDTO.specificResources.securityListFragmentTitleResId > 0)
        {
            setActionBarTitle(providerDTO.specificResources.securityListFragmentTitleResId);
        }
        else if (providerDTO != null)
        {
            setActionBarTitle(providerDTO.name);
        }
        else
        {
            setActionBarTitle(R.string.provider_security_list_title);
        }
    }

    @Override protected AdapterView.OnItemClickListener createOnItemClickListener()
    {
        return new OnSecurityViewClickListener();
    }

    @Override protected ListAdapter createSecurityItemViewAdapter()
    {
        return securityItemViewAdapterFactory.create(getActivity(), providerId);
    }

    @Override public int getSecurityIdListLoaderId()
    {
        return SECURITY_ID_LIST_LOADER_ID + providerId.key;
    }

    @Override @NotNull public ProviderSecurityListType getSecurityListType(int page)
    {
        return new BasicProviderSecurityListType(providerId, page, perPage);
    }

    protected DTOCacheNew.Listener<ProviderId, ProviderDTO> createProviderCacheListener()
    {
        return new ProviderSecurityListFragmentProviderCacheListener();
    }

    protected class ProviderSecurityListFragmentProviderCacheListener implements DTOCacheNew.Listener<ProviderId, ProviderDTO>
    {
        @Override public void onDTOReceived(@NotNull ProviderId key, @NotNull ProviderDTO value)
        {
            if (key.equals(ProviderSecurityListFragment.this.providerId))
            {
                ProviderSecurityListFragment.this.linkWith(value, true);
            }
        }

        @Override public void onErrorThrown(@NotNull ProviderId key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_provider_info));
        }
    }

    protected class ProviderSecurityListLoaderCallback extends SecurityListLoaderCallback
    {
        @Override public Loader<SecurityCompactDTOList> onCreateLoader(int id, Bundle args)
        {
            if (id == getSecurityIdListLoaderId())
            {
                SecurityListPagedLoader loader;
                loader = new SecurityListPagedLoader(getActivity());
                loader.setQueryingChangedListenerWeak(queryingChangedListener);
                loader.setNoMorePagesChangedListenerWeak(noMorePagesChangedListener);
                return loader;
            }
            throw new IllegalStateException("Unhandled loader id " + id);
        }
    }

    private class OnSecurityViewClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
        }
    }
}
