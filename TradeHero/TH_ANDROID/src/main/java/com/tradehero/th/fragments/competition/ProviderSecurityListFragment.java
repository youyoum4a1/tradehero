package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.security.SecuritySearchProviderFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.provider.ProviderSpecificKnowledgeDTO;
import com.tradehero.th.models.provider.ProviderSpecificKnowledgeFactory;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.utils.DeviceUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ProviderSecurityListFragment extends SecurityListFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = ProviderSecurityListFragment.class.getName() + ".providerId";
    public final static int SECURITY_ID_LIST_LOADER_ID = 2531;

    // TODO sort warrants
    @NotNull protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    protected ProviderSpecificResourcesDTO providerSpecificResourcesDTO;
    protected ProviderSpecificKnowledgeDTO providerSpecificKnowledgeDTO;
    @Inject ProviderCache providerCache;
    @Inject ProviderUtil providerUtil;
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    @Inject ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;
    @Inject SecurityItemViewAdapterFactory securityItemViewAdapterFactory;

    private DTOCacheNew.Listener<ProviderId, ProviderDTO> providerCacheListener;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private BaseWebViewFragment webViewFragment;

    ActionBar actionBar;
    private MenuItem wizardButton;

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
        this.providerSpecificResourcesDTO = this.providerSpecificResourcesFactory.createResourcesDTO(providerId);
        this.providerSpecificKnowledgeDTO = this.providerSpecificKnowledgeFactory.createKnowledge(providerId);

        this.providerCacheListener = createProviderCacheListener();
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
            boolean visible;
            if (providerSpecificKnowledgeDTO != null && providerSpecificKnowledgeDTO.hasWizard != null)
            {
                visible = providerSpecificKnowledgeDTO.hasWizard;
            }
            else
            {
                visible = providerDTO != null && providerDTO.hasWizard();
            }
            wizardButton.setVisible(visible);
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
        this.webViewTHIntentPassedListener = null;
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

    private void pushWizardElement()
    {
        Bundle args = new Bundle();
        args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL, providerUtil.getWizardPage(providerId) + "&previous=whatever");
        args.putBoolean(CompetitionWebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, false);
        this.webViewFragment = getDashboardNavigator().pushFragment(
                CompetitionWebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    private void pushSearchFragment()
    {
        Bundle args = new Bundle();
        SecuritySearchProviderFragment.putProviderId(args, providerId);
        SecuritySearchProviderFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
        getDashboardNavigator().pushFragment(SecuritySearchProviderFragment.class, args);
    }

    protected DTOCacheNew.Listener<ProviderId, ProviderDTO> createProviderCacheListener()
    {
        return new ProviderSecurityListFragmentProviderCacheListener();
    }

    protected class ProviderSecurityListFragmentProviderCacheListener implements DTOCacheNew.Listener<ProviderId, ProviderDTO>
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
            BuySellFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
            args.putBundle(BuySellFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
            // TODO use other positions
            getDashboardNavigator().pushFragment(BuySellFragment.class, args);
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

        @Override protected Navigator getNavigator()
        {
            return ProviderSecurityListFragment.this.getDashboardNavigator();
        }

        @Override protected Class<?> getClassToPop()
        {
            return ProviderSecurityListFragment.class;
        }
    }
}
