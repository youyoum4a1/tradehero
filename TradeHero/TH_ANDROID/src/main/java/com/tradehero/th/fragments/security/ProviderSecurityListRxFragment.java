package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapter;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.CompetitionWebFragmentTHIntentPassedListener;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.utils.DeviceUtil;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ProviderSecurityListRxFragment
        extends SecurityListRxFragment
{
    @Inject ProviderCacheRx providerCache;
    @Inject ProviderUtil providerUtil;

    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    private THIntentPassedListener webViewTHIntentPassedListener;
    private BaseWebViewFragment webViewFragment;

    public static void putProviderId(@NonNull Bundle bundle, @NonNull ProviderId providerId)
    {
        bundle.putBundle(BUNDLE_PROVIDER_ID_KEY, providerId.getArgs());
    }

    @NonNull private static ProviderId getProviderId(@NonNull Bundle bundle)
    {
        return new ProviderId(bundle.getBundle(BUNDLE_PROVIDER_ID_KEY));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
        this.webViewTHIntentPassedListener = new ProviderSecurityListWebViewTHIntentPassedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_provider_security_list, container, false);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProviderDTO();
        nearEndScrollListener.lowerEndFlag();
        nearEndScrollListener.activateEnd();
        requestDtos();
    }

    @Override public void onResume()
    {
        super.onResume();
        // We came back into view so we have to forget the web fragment
        if (this.webViewFragment != null)
        {
            this.webViewFragment.setThIntentPassedListener(null);
        }
        this.webViewFragment = null;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        displayTitle();
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if (providerDTO != null)
        {
            getActivity().getMenuInflater().inflate(R.menu.provider_security_list_menu, menu);

            MenuItem wizardButton = menu.findItem(R.id.btn_wizard);
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

    @NonNull @Override protected PagedDTOAdapter<SecurityCompactDTO> createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(getActivity(), R.layout.trending_security_item);
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return providerId != null;
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new BasicProviderSecurityListType(providerId, page, perPage);
    }

    protected void fetchProviderDTO()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                providerCache.get(this.providerId)
                        .map(new PairGetSecond<ProviderId, ProviderDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ProviderDTO>()
                        {
                            @Override public void call(ProviderDTO provider)
                            {
                                ProviderSecurityListRxFragment.this.linkWith(provider);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_provider_info))));
    }

    protected void linkWith(@NonNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        getActivity().invalidateOptionsMenu();
        getActivity().supportInvalidateOptionsMenu();
        displayTitle();
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

    @Override protected void handleDtoClicked(SecurityCompactDTO clicked)
    {
        super.handleDtoClicked(clicked);
        Bundle args = new Bundle();
        BuySellFragment.putSecurityId(args, clicked.getSecurityId());
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
        args.putBundle(BuySellFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
        navigator.get().pushFragment(SecurityCompactDTOUtil.fragmentFor(clicked), args);
    }

    protected void pushWizardElement()
    {
        Bundle args = new Bundle();
        CompetitionWebViewFragment.putUrl(args, providerUtil.getWizardPage(providerId) + "&previous=whatever");
        CompetitionWebViewFragment.putIsOptionMenuVisible(args, false);

        this.webViewFragment = navigator.get().pushFragment(
                CompetitionWebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    protected void pushSearchFragment()
    {
        Bundle args = new Bundle();
        populateSearchArguments(args);
        navigator.get().pushFragment(SecuritySearchProviderFragment.class, args);
    }

    protected void populateSearchArguments(@NonNull Bundle args)
    {
        SecuritySearchProviderFragment.putProviderId(args, providerId);
        if (providerDTO != null
                && providerDTO.associatedPortfolio != null
                && providerDTO.associatedPortfolio.assetClass != null)
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
            return ProviderSecurityListRxFragment.this.getApplicablePortfolioId();
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
            return ProviderSecurityListRxFragment.class;
        }
    }
}
