package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.security.SecuritySearchProviderFragment;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;

public class ProviderSecurityListFragment extends SecurityListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = ProviderSecurityListFragment.class.getName() + ".providerId";
    public final static int SECURITY_ID_LIST_LOADER_ID = 2531;

    // TODO sort warrants
    @NonNull protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject SecurityCompactDTOUtil securityCompactDTOUtil;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private BaseWebViewFragment webViewFragment;

    private MenuItem wizardButton;
    @NonNull protected SubscriptionList subscriptions;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull private static ProviderId getProviderId(@NonNull Bundle args)
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
        this.webViewTHIntentPassedListener = new ProviderSecurityListWebViewTHIntentPassedListener();
        this.subscriptions = new SubscriptionList();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_provider_security_list, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        displayTitle();
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if(providerDTO != null)
        {
            getActivity().getMenuInflater().inflate(R.menu.provider_security_list_menu, menu);

            wizardButton = menu.findItem(R.id.btn_wizard);
            if (wizardButton != null)
            {
                wizardButton.setVisible(providerDTO.hasWizard());
            }
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_wizard:
                pushWizardElement();
                return true;

            case R.id.btn_search:
                pushSearchFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        // We came back into view so we have to forget the web fragment
        if (this.webViewFragment != null)
        {
            this.webViewFragment.setThIntentPassedListener(null);
        }
        this.webViewFragment = null;
    }

    @Override public void onStop()
    {
        subscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        DeviceUtil.dismissKeyboard(getActivity());
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    protected void fetchProviderDTO()
    {
        subscriptions.add(AndroidObservable.bindFragment(this, providerCache.get(this.providerId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProviderCacheObserver()));
    }

    protected void prepareSecurityLoader()
    {
        getActivity().getSupportLoaderManager().initLoader(getSecurityIdListLoaderId(), null, new ProviderSecurityListLoaderCallback());
    }

    protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;

        getActivity().invalidateOptionsMenu();
        getActivity().supportInvalidateOptionsMenu();

        if (andDisplay)
        {
            displayTitle();
        }
    }

    protected void displayTitle()
    {
        if (providerDTO != null)
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
        return new SimpleSecurityItemViewAdapter(
                getActivity(),
                R.layout.trending_security_item);
    }

    @Override public int getSecurityIdListLoaderId()
    {
        return SECURITY_ID_LIST_LOADER_ID + providerId.key;
    }

    @Override @NonNull public ProviderSecurityListType getSecurityListType(int page)
    {
        return new BasicProviderSecurityListType(providerId, page, perPage);
    }

    private void pushWizardElement()
    {
        Bundle args = new Bundle();
        CompetitionWebViewFragment.putUrl(args, providerUtil.getWizardPage(providerId) + "&previous=whatever");
        CompetitionWebViewFragment.putIsOptionMenuVisible(args, false);

        this.webViewFragment = navigator.get().pushFragment(
                CompetitionWebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    private void pushSearchFragment()
    {
        Bundle args = new Bundle();
        SecuritySearchProviderFragment.putProviderId(args, providerId);
        if(providerDTO != null && providerDTO.associatedPortfolio. assetClass != null)
        {
            SecuritySearchProviderFragment.putAssetClass(args, providerDTO.associatedPortfolio.assetClass);
        }
        else
        {
            SecuritySearchProviderFragment.putAssetClass(args, AssetClass.STOCKS);
        }
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            SecuritySearchProviderFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        navigator.get().pushFragment(SecuritySearchProviderFragment.class, args);
    }

    protected Observer<Pair<ProviderId, ProviderDTO>> createProviderCacheObserver()
    {
        return new ProviderSecurityListFragmentProviderCacheObserver();
    }

    protected class ProviderSecurityListFragmentProviderCacheObserver implements Observer<Pair<ProviderId, ProviderDTO>>
    {
        @Override public void onNext(Pair<ProviderId, ProviderDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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
            SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);
            Bundle args = new Bundle();
            BuySellFragment.putSecurityId(args, securityCompactDTO.getSecurityId());
            BuySellFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
            args.putBundle(BuySellStockFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
            navigator.get().pushFragment(securityCompactDTOUtil.fragmentFor(securityCompactDTO), args);
        }
    }

    private class ProviderSecurityListWebViewTHIntentPassedListener extends CompetitionWebFragmentTHIntentPassedListener
    {
        public ProviderSecurityListWebViewTHIntentPassedListener()
        {
            super();
        }

        @Override protected BaseWebViewFragment getApplicableWebViewFragment()
        {
            return webViewFragment;
        }

        @Override protected OwnedPortfolioId getApplicablePortfolioId()
        {
            return ProviderSecurityListFragment.this.getApplicablePortfolioId();
        }

        @Override protected ProviderId getProviderId()
        {
            return providerId;
        }

        @Override protected DashboardNavigator getNavigator()
        {
            return navigator.get();
        }

        @Override protected Class<?> getClassToPop()
        {
            return ProviderSecurityListFragment.class;
        }
    }
}
