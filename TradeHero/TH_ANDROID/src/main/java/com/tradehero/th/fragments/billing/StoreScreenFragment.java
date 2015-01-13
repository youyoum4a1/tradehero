package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTOList;
import com.tradehero.th.fragments.billing.store.StoreItemFactory;
import com.tradehero.th.fragments.billing.store.StoreItemHasFurtherDTO;
import com.tradehero.th.fragments.billing.store.StoreItemPromptPurchaseDTO;
import com.tradehero.th.fragments.social.follower.FollowerRevenueReportFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import timber.log.Timber;

@Routable({
        "store", "store/:action"
})
public class StoreScreenFragment extends BasePurchaseManagerFragment
        implements WithTutorial
{
    public static boolean alreadyNotifiedNeedCreateAccount = false;

    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @Inject StoreItemFactory storeItemFactory;

    @RouteProperty("action") Integer productDomainIdentifierOrdinal;

    @InjectView(R.id.store_option_list) protected ListView listView;
    private Subscription testAvailableSubscription;
    private StoreItemAdapter storeItemAdapter;
    private Subscription storeItemSubscription;
    @NonNull protected SubscriptionList subscriptions;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        subscriptions = new SubscriptionList();
        thRouter.inject(this);
        storeItemAdapter = new StoreItemAdapter(getActivity());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        listView.setAdapter(storeItemAdapter);
        listView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.store_option_menu_title);  // Add the changing cute icon
        setActionBarSubtitle(userInteractorRx.getName());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Store));

        storeItemAdapter.clear();
        unsubscribe(storeItemSubscription);
        storeItemSubscription = AndroidObservable.bindFragment(
                this,
                storeItemFactory.createAll(StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS)
                        .take(1))
                .subscribe(new EmptyObserver<StoreItemDTOList>()
                {
                    @Override public void onNext(StoreItemDTOList storeItemDTOs)
                    {
                        storeItemAdapter.clear();
                        storeItemAdapter.addAll(storeItemDTOs);
                        storeItemAdapter.notifyDataSetChanged();
                    }
                });

        cancelOthersAndShowBillingAvailable();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onStop()
    {
        subscriptions.unsubscribe();
        unsubscribe(storeItemSubscription);
        storeItemSubscription = null;
        unsubscribe(testAvailableSubscription);
        testAvailableSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        storeItemAdapter = null;
        super.onDestroy();
    }

    public void cancelOthersAndShowBillingAvailable()
    {
        if (alreadyNotifiedNeedCreateAccount)
        {
            return;
        }

        unsubscribe(testAvailableSubscription);
        //noinspection unchecked
        testAvailableSubscription = AndroidObservable.bindFragment(
                this,
                userInteractorRx.testAndClear())
                .finallyDo(() -> alreadyNotifiedNeedCreateAccount = true)
                .subscribe(
                        pair -> {},
                        error -> {});
    }

    @Override protected void handleReceivedPortfolioCompactList(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        super.handleReceivedPortfolioCompactList(portfolioCompactDTOs);
        launchRoutedAction();
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
                //noinspection unchecked
                subscriptions.add(AndroidObservable.bindFragment(
                        this,
                        userInteractorRx.purchase(ProductIdentifierDomain.values()[productDomainIdentifierOrdinal]))
                        .subscribe(
                                pair -> {},
                                error -> THToast.show(new THException((Throwable) error))
                        ));
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.store_option_list)
    protected void onStoreListItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        handlePositionClicked((StoreItemDTO) adapterView.getItemAtPosition(position));
    }

    private void handlePositionClicked(StoreItemDTO clickedItem)
    {
        if (clickedItem instanceof StoreItemPromptPurchaseDTO)
        {
            //noinspection unchecked
            AndroidObservable.bindFragment(
                    this,
                    userInteractorRx.purchaseAndClear(((StoreItemPromptPurchaseDTO) clickedItem).productIdentifierDomain))
                    .subscribe(new EmptyObserver<PurchaseResult>()
                    {
                        @Override public void onNext(PurchaseResult args)
                        {
                            handlePurchaseFinished(args);
                        }

                        @Override public void onError(Throwable e)
                        {
                            handlePurchaseFailed(e);
                        }
                    });
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

    protected void handlePurchaseFinished(@NonNull PurchaseResult purchaseResult)
    {
        THToast.show("Purchase done");
    }

    protected void handlePurchaseFailed(@NonNull Throwable throwable)
    {
        THToast.show(new THException(throwable));
        Timber.e(throwable, "Purchase failed");
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
        else if (fragmentClass.equals(FollowerRevenueReportFragment.class))
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
        pushFragment(FollowerRevenueReportFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        navigator.get().pushFragment(fragmentClass, bundle);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }
}
