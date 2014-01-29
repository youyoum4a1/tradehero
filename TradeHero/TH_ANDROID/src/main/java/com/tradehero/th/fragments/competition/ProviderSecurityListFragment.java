package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.ProviderConstants;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.ProviderSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.tradehero.th.fragments.security.SecurityItemViewAdapter;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.intent.security.SecurityPushBuyIntent;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
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
    protected ProviderSpecificResourcesDTO providerSpecificResourcesDTO;
    @Inject protected ProviderCache providerCache;
    private DTOCache.Listener<ProviderId, ProviderDTO> providerCacheListener;
    private DTOCache.GetOrFetchTask<ProviderId, ProviderDTO> providerCacheFetchTask;
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    @Inject SecurityItemLayoutFactory securityItemLayoutFactory;
    private THIntentPassedListener webViewTHIntentPassedListener;
    private WebViewFragment webViewFragment;

    ActionBar actionBar;
    private MenuItem wizardButton;

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
        this.providerSpecificResourcesDTO = this.providerSpecificResourcesFactory.createResourcesDTO(providerId);

        this.providerCacheListener = new ProviderSecurityListFragmentProviderCacheListener();
        this.webViewTHIntentPassedListener = new ProviderSecurityListWebViewTHIntentPassedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_provider_security_list, container, false);
        initViews(view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, inflater);
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.securityListFragmentTitleResId > 0)
        {
            actionBar.setTitle(providerSpecificResourcesDTO.securityListFragmentTitleResId);
        }
        else
        {
            actionBar.setTitle(R.string.provider_security_list_title);
        }

        inflater.inflate(R.menu.provider_security_list_menu, menu);

        wizardButton = menu.findItem(R.id.btn_wizard);
        if (wizardButton != null)
        {
            wizardButton.setVisible(providerDTO != null && providerDTO.hasWizard());
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_wizard:
                pushWizardElement();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStart()
    {
        THLog.d(TAG, "onStart");
        super.onStart();
        this.detachProviderFetchTask();
        this.providerCacheFetchTask = providerCache.getOrFetch(this.providerId, this.providerCacheListener);
        this.providerCacheFetchTask.execute();
        //forceInitialLoad();
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
        THLog.d(TAG, "onStop");
        this.detachProviderFetchTask();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        this.providerCacheListener = null;
        this.webViewTHIntentPassedListener = null;
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
        }
    }

    @Override protected AdapterView.OnItemClickListener createOnItemClickListener()
    {
        return new OnSecurityViewClickListener();
    }

    @Override protected SecurityItemViewAdapter createSecurityItemViewAdapter()
    {
        if (providerId != null && providerId.key.equals(ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS))
        {
            THLog.d(TAG, "Macquarie adapter");
            return new MacquarieWarrantItemViewAdapter(
                    getActivity(),
                    getActivity().getLayoutInflater(),
                    securityItemLayoutFactory.getProviderLayout(providerId));
        }
        THLog.d(TAG, "Regular adapter");
        return new SimpleSecurityItemViewAdapter(
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

    private void pushWizardElement()
    {
        Bundle args = new Bundle();
        args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getWizardPage(providerId) + "&previous=whatever");
        args.putBoolean(WebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, false);
        this.webViewFragment = (WebViewFragment) navigator.pushFragment(WebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
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

    protected class ProviderSecurityListLoaderCallback extends SecurityListLoaderCallback
    {
        @Override public Loader<SecurityIdList> onCreateLoader(int id, Bundle args)
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
            args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());
            args.putBundle(BuySellFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
            args.putBundle(BuySellFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
            // TODO use other positions
            navigator.pushFragment(BuySellFragment.class, args);

            // startActivity(new SecurityBuyIntent(securityCompactDTO)); // Example using external navigation
        }
    }

    private class ProviderSecurityListWebViewTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            if (thIntent instanceof ProviderPageIntent)
            {
                THLog.d(TAG, "Intent is ProviderPageIntent");
                if (webViewFragment != null)
                {
                    THLog.d(TAG, "Passing on " + ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webViewFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                }
                else
                {
                    THLog.d(TAG, "WebFragment is null");
                }
            }
            else if (thIntent instanceof SecurityPushBuyIntent)
            {
                handleSecurityPushBuyIntent((SecurityPushBuyIntent) thIntent);
            }
            else if (thIntent == null)
            {
                navigator.popFragment();
            }
            else
            {
                THLog.w(TAG, "Unhandled intent " + thIntent);
            }
        }

        protected void handleSecurityPushBuyIntent(SecurityPushBuyIntent thIntent)
        {
            // We are probably coming back from the wizard
            getNavigator().popFragment(ProviderSecurityListFragment.class.getName());
            // Now moving on
            Bundle argsBundle = thIntent.getBundle();
            if (thIntent.getActionFragment().equals(BuySellFragment.class))
            {
                argsBundle.putBundle(BuySellFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
                argsBundle.putBundle(BuySellFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
            }
            getNavigator().pushFragment(thIntent.getActionFragment(), argsBundle,
                    new int[] {
                            R.anim.slide_right_in, R.anim.alpha_out,
                            R.anim.slide_left_in, R.anim.slide_right_out
                    }, null);
            THLog.d(TAG, "onIntentPassed " + thIntent);
        }
    }
}
