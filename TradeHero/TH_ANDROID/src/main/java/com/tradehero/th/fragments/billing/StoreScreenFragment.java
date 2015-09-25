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
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.purchase.PurchaseResult;
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.billing.THProductDetail;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.store.StoreItemDisplayDTO;
import com.tradehero.th.fragments.billing.store.StoreItemProductDisplayDTO;
import com.tradehero.th.fragments.billing.store.StoreItemRestoreDisplayDTO;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
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

    private StoreItemAdapter storeItemAdapter;
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
        storeItemAdapter = new StoreItemAdapter();
        storeItemAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<StoreItemDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<StoreItemDisplayDTO> viewHolder, StoreItemDisplayDTO object)
            {
                if (object instanceof StoreItemProductDisplayDTO)
                {
                    unsubscribe(purchaseSubscription);

                    //noinspection unchecked
                    purchaseSubscription = AppObservable.bindSupportFragment(
                            StoreScreenFragment.this,
                            userInteractorRx.createPurchaseOrder(((StoreItemProductDisplayDTO) object).productDetail)
                                    .flatMap(new Func1<PurchaseOrder, Observable<PurchaseResult>>()
                                    {
                                        @Override public Observable<PurchaseResult> call(PurchaseOrder o)
                                        {
                                            return userInteractorRx.purchase(o);
                                        }
                                    }))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<PurchaseResult>()
                                    {
                                        @Override public void call(PurchaseResult ignored)
                                        {
                                            userProfileCache.get(currentUserId.toUserBaseKey());
                                            portfolioCompactListCache.get(currentUserId.toUserBaseKey());
                                        }
                                    },
                                    new TimberAndToastOnErrorAction1("Purchase failed"));
                }
                else if (object instanceof StoreItemRestoreDisplayDTO)
                {
                    unsubscribe(purchaseSubscription);
                    //noinspection unchecked
                    purchaseSubscription = AppObservable.bindSupportFragment(
                            StoreScreenFragment.this,
                            userInteractorRx.restorePurchasesAndClear(true))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    new Action1<PurchaseResult>()
                                    {
                                        @Override public void call(PurchaseResult ignored)
                                        {
                                            userProfileCache.get(currentUserId.toUserBaseKey());
                                            portfolioCompactListCache.get(currentUserId.toUserBaseKey());
                                        }
                                    },
                                    new TimberAndToastOnErrorAction1("Restore failed"));
                }
            }
        });
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
        listView.addItemDecoration(new TypedRecyclerAdapter.DividerItemDecoration(getActivity()));
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
                .map(new Func1<List<THProductDetail>, List<StoreItemDisplayDTO>>()
                {
                    @Override public List<StoreItemDisplayDTO> call(List<THProductDetail> thProductDetails)
                    {
                        ArrayList<StoreItemDisplayDTO> dtos = new ArrayList<>(thProductDetails.size());
                        for (THProductDetail productDetail : thProductDetails)
                        {
                            dtos.add(new StoreItemProductDisplayDTO(productDetail));
                        }
                        dtos.add(new StoreItemRestoreDisplayDTO());
                        return dtos;
                    }
                })
                .subscribe(new Action1<List<StoreItemDisplayDTO>>()
                {
                    @Override public void call(List<StoreItemDisplayDTO> dtos)
                    {
                        storeItemAdapter.addAll(dtos);
                    }
                }, new TimberOnErrorAction1("Failed to fetch products for Store")));

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

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }
}
