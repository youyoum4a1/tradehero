package com.tradehero.th.fragments.billing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.billing.THProductDetail;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import timber.log.Timber;

@Routable({
        "store", "store/:action"
})
public class StoreScreenFragment extends BaseFragment
        implements WithTutorial
{
    public static boolean alreadyNotifiedNeedCreateAccount = false;

    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject protected THBillingInteractorRx userInteractorRx;

    @RouteProperty("action") Integer productDomainIdentifierOrdinal;

    @Bind(R.id.store_option_list) protected RecyclerView listView;

    private ProductDetailRecyclerAdapter storeItemAdapter;
    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    @Nullable protected Subscription purchaseSubscription;

    public static void registerAliases(@NonNull THRouter router)
    {
        router.registerAlias("cash", "store/" + ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR.ordinal());
        router.registerAlias("store/cash", "store/" + ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR.ordinal());
        router.registerAlias("credits", "store/" + ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS.ordinal());
        router.registerAlias("store/credits", "store/" + ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS.ordinal());
        router.registerAlias("store/reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
        router.registerAlias("reset-portfolio", "store/" + ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO.ordinal());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        storeItemAdapter = new ProductDetailRecyclerAdapter();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        thRouter.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setHasFixedSize(true);
        listView.setAdapter(storeItemAdapter);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.store_option_menu_title);  // Add the changing cute icon
        if (!Constants.RELEASE)
        {
            setActionBarSubtitle(userInteractorRx.getName());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Store));

        fetchUserProfile();

        onStopSubscriptions.add(getProductsObservable()
                .subscribe(new Action1<List<THProductDetail>>()
                {
                    @Override public void call(List<THProductDetail> thProductDetails)
                    {
                        storeItemAdapter.addAll(thProductDetails);
                    }
                }));

        cancelOthersAndShowBillingAvailable();
    }

    public Observable<List<THProductDetail>> getProductsObservable()
    {
        return userInteractorRx.listProduct();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        storeItemAdapter = null;
        super.onDetach();
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                           {
                               @Override public void call(Pair<UserBaseKey, UserProfileDTO> userProfileDTO)
                               {
                                   purchaseApplicableOwnedPortfolioId =
                                           new OwnedPortfolioId(userProfileDTO.second.portfolio.id, currentUserId.get());
                                   StoreScreenFragment.this.launchRoutedAction();
                               }
                           },
                        new EmptyAction1<Throwable>()));
    }

    public void cancelOthersAndShowBillingAvailable()
    {
        if (alreadyNotifiedNeedCreateAccount)
        {
            return;
        }

        //noinspection unchecked
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userInteractorRx.testAndClear())
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        alreadyNotifiedNeedCreateAccount = true;
                    }
                })
                .subscribe(
                        new EmptyAction1<BillingTestResult>(),
                        new EmptyAction1<Throwable>()));
    }

    protected void launchRoutedAction()
    {
        if (productDomainIdentifierOrdinal != null)
        {
            OwnedPortfolioId applicablePortfolioId = purchaseApplicableOwnedPortfolioId;
            if (applicablePortfolioId == null)
            {
                Timber.e(new Exception("Null portfolio id"), "Even when received portfolio list");
            }
            else
            {
                //noinspection unchecked
                onStopSubscriptions.add(AppObservable.bindSupportFragment(
                        this,
                        userInteractorRx.purchase(ProductIdentifierDomain.values()[productDomainIdentifierOrdinal]))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                Actions.empty(),
                                new ToastOnErrorAction1()));
                productDomainIdentifierOrdinal = null;
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    //@OnItemClick(R.id.store_option_list)
    protected void onStoreListItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        //StoreItemDTO clickedItem = (StoreItemDTO) adapterView.getItemAtPosition(position);
        //if (clickedItem instanceof StoreItemPromptPurchaseDTO)
        //{
        //    unsubscribe(purchaseSubscription);
        //    //noinspection unchecked
        //    purchaseSubscription = AppObservable.bindSupportFragment(
        //            this,
        //            userInteractorRx.purchaseAndClear(((StoreItemPromptPurchaseDTO) clickedItem).productIdentifierDomain))
        //            .observeOn(AndroidSchedulers.mainThread())
        //            .subscribe(
        //                    new Action1<PurchaseResult>()
        //                    {
        //                        @Override public void call(PurchaseResult ignored)
        //                        {
        //                            userProfileCache.get(currentUserId.toUserBaseKey());
        //                            portfolioCompactListCache.get(currentUserId.toUserBaseKey());
        //                        }
        //                    },
        //                    new TimberAndToastOnErrorAction1("Purchase failed"));
        //}
        //else if (clickedItem instanceof StoreItemRestoreDTO)
        //{
        //    unsubscribe(purchaseSubscription);
        //    //noinspection unchecked
        //    purchaseSubscription = AppObservable.bindSupportFragment(
        //            this,
        //            userInteractorRx.restorePurchasesAndClear(true))
        //            .observeOn(AndroidSchedulers.mainThread())
        //            .subscribe(
        //                    new Action1<PurchaseResult>()
        //                    {
        //                        @Override public void call(PurchaseResult ignored)
        //                        {
        //                            userProfileCache.get(currentUserId.toUserBaseKey());
        //                            portfolioCompactListCache.get(currentUserId.toUserBaseKey());
        //                        }
        //                    },
        //                    new TimberAndToastOnErrorAction1("Restore failed"));
        //}
        //else if (clickedItem instanceof StoreItemHasFurtherDTO)
        //{
        //    StoreItemHasFurtherDTO furtherDTO = (StoreItemHasFurtherDTO) clickedItem;
        //    if (furtherDTO.furtherActivity != null)
        //    {
        //        navigator.get().launchActivity(furtherDTO.furtherActivity);
        //    }
        //    else if (furtherDTO.furtherFragment != null)
        //    {
        //        //TODO
        //        //if (furtherDTO.furtherFragment.equals(HeroManagerFragment.class))
        //        //{
        //        //    Bundle bundle = new Bundle();
        //        //    HeroManagerFragment.putFollowerId(bundle, currentUserId.toUserBaseKey());
        //        //    navigator.get().pushFragment(HeroManagerFragment.class, bundle);
        //        //}
        //        //else if (furtherDTO.furtherFragment.equals(FollowerRevenueReportFragment.class))
        //        //{
        //        //    navigator.get().pushFragment(FollowerRevenueReportFragment.class);
        //        //}
        //        //else
        //        //{
        //        throw new IllegalArgumentException("Unhandled class " + furtherDTO.furtherFragment);
        //        //}
        //    }
        //    else
        //    {
        //        throw new IllegalArgumentException("Unhandled situation where both activity and fragment are null");
        //    }
        //}
        //else
        //{
        //    throw new IllegalArgumentException("Unhandled type " + clickedItem);
        //}
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }
}
