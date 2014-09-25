package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemFactory;
import com.tradehero.th.fragments.billing.store.StoreItemHasFurtherDTO;
import com.tradehero.th.fragments.billing.store.StoreItemPromptPurchaseDTO;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Routable({
        "store", "store/:action"
})
public class StoreScreenFragment extends BasePurchaseManagerFragment
    implements WithTutorial
{
    public static boolean alreadyNotifiedNeedCreateAccount = false;
    protected Integer showBillingAvailableRequestCode;
    protected Integer showProductRequestCode;

    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @Inject StoreItemFactory storeItemFactory;
    @Inject protected THBillingInteractor userInteractor;
    @Inject DashboardNavigator navigator;

    @RouteProperty("action") Integer productDomainIdentifierOrdinal;

    @InjectView(R.id.store_option_list) protected ListView listView;
    private StoreItemAdapter storeItemAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        storeItemAdapter = new StoreItemAdapter(getActivity());
        if (listView != null)
        {
            listView.setAdapter(storeItemAdapter);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.store_option_menu_title);  // Add the changing cute icon
        setActionBarSubtitle(userInteractor.getName());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Store));

        storeItemAdapter.clear();
        storeItemAdapter.addAll(storeItemFactory.createAll(StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS));
        storeItemAdapter.notifyDataSetChanged();

        cancelOthersAndShowBillingAvailable();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        storeItemAdapter = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    public void cancelOthersAndShowBillingAvailable()
    {
        if (alreadyNotifiedNeedCreateAccount)
        {
            return;
        }

        if (showBillingAvailableRequestCode != null)
        {
            userInteractor.forgetRequestCode(showBillingAvailableRequestCode);
        }
        showBillingAvailableRequestCode = showBillingAvailable();
        alreadyNotifiedNeedCreateAccount = true;
    }

    public int showBillingAvailable()
    {
        return userInteractor.run(getShowBillingAvailableRequest());
    }

    public THUIBillingRequest getShowBillingAvailableRequest()
    {
        BaseTHUIBillingRequest.Builder request = uiBillingRequestBuilderProvider.get();
        request.startWithProgressDialog(false);
        request.popIfBillingNotAvailable(!alreadyNotifiedNeedCreateAccount);
        request.testBillingAvailable(true);
        return request.build();
    }

    @Override protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new StoreScreenFragmentPortfolioCompactListFetchListener();
    }

    protected class StoreScreenFragmentPortfolioCompactListFetchListener extends BasePurchaseManagementPortfolioCompactListFetchListener
    {
        protected StoreScreenFragmentPortfolioCompactListFetchListener()
        {
            super();
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            super.onDTOReceived(key, value);
            launchRoutedAction();
        }
    }

    protected void launchRoutedAction()
    {
        if (productDomainIdentifierOrdinal != null)
        {
            OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId == null)
            {
                Timber.e(new Exception("Null portfolio id"), "Even when received portfolio list");
            }
            else
            {
                detachRequestCode();
                THUIBillingRequest uiRequest = (THUIBillingRequest) uiBillingRequestBuilderProvider.get()
                        .domainToPresent(ProductIdentifierDomain.values()[productDomainIdentifierOrdinal])
                        .applicablePortfolioId(applicablePortfolioId)
                        .startWithProgressDialog(true)
                        .popIfBillingNotAvailable(true)
                        .popIfProductIdentifierFetchFailed(true)
                        .popIfInventoryFetchFailed(true)
                        .build();
                //noinspection unchecked
                requestCode = userInteractor.run(uiRequest);
            }
        }
    }

    @OnItemClick(R.id.store_option_list)
    protected void onStoreListItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        handlePositionClicked((StoreItemDTO) adapterView.getItemAtPosition(position));
    }

    private void handlePositionClicked(StoreItemDTO clickedItem)
    {
        if (clickedItem instanceof StoreItemPromptPurchaseDTO)
        {
            if (showProductRequestCode != null)
            {
                userInteractor.forgetRequestCode(showProductRequestCode);
            }
            //noinspection unchecked
            showProductRequestCode = userInteractor.run(
                    uiBillingRequestBuilderProvider.get()
                            .domainToPresent(((StoreItemPromptPurchaseDTO) clickedItem).productIdentifierDomain)
                            .applicablePortfolioId(getApplicablePortfolioId())
                            .startWithProgressDialog(true)
                            .popIfBillingNotAvailable(true)
                            .popIfProductIdentifierFetchFailed(true)
                            .popIfInventoryFetchFailed(true)
                            .doPurchase(true)
                            .popIfPurchaseFailed(true)
                            .popIfReportFailed(true)
                            .build());
        }
        else if (clickedItem instanceof StoreItemHasFurtherDTO)
        {
            pushFragment(((StoreItemHasFurtherDTO) clickedItem).furtherFragment);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + clickedItem);
        }
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        if (fragmentClass.equals(AlertManagerFragment.class))
        {
            pushStockAlertFragment();
        }
        else if (fragmentClass.equals(HeroManagerFragment.class))
        {
            pushHeroFragment();
        }
        else if (fragmentClass.equals(FollowerManagerFragment.class))
        {
            pushFollowerFragment();
        }
        else
        {
            throw new IllegalArgumentException("Unhandled class " + fragmentClass);
        }
    }

    private void pushStockAlertFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(AlertManagerFragment.BUNDLE_KEY_USER_ID, currentUserId.get());
        pushFragment(AlertManagerFragment.class, bundle);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            HeroManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            //FollowerManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        navigator.pushFragment(fragmentClass, bundle);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }
}
