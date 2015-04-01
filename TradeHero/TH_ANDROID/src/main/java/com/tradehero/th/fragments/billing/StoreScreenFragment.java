package com.tradehero.th.fragments.billing;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
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
import com.tradehero.common.billing.tester.BillingTestResult;
import com.tradehero.common.utils.THToast;
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
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTOList;
import com.tradehero.th.fragments.billing.store.StoreItemFactory;
import com.tradehero.th.fragments.billing.store.StoreItemHasFurtherDTO;
import com.tradehero.th.fragments.billing.store.StoreItemPromptPurchaseDTO;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.social.follower.FollowerRevenueReportFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
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
    @Inject SystemStatusCache systemStatusCache;
    @Inject UserProfileCacheRx userProfileCacheRx;

    @RouteProperty("action") Integer productDomainIdentifierOrdinal;

    @InjectView(R.id.store_option_list) protected ListView listView;
    private StoreItemAdapter storeItemAdapter;
    @Inject protected THBillingInteractorRx userInteractorRx;
    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        thRouter.inject(this);
        storeItemAdapter = new StoreItemAdapter(activity);
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
        storeItemAdapter.clear();
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS)
                        .take(1))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<StoreItemDTOList>()
                        {
                            @Override public void call(StoreItemDTOList storeItemDTOs)
                            {
                                storeItemAdapter.clear();
                                storeItemAdapter.addAll(storeItemDTOs);
                                storeItemAdapter.notifyDataSetChanged();
                            }
                        },
                        new ToastOnErrorAction()));

        cancelOthersAndShowBillingAvailable();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        storeItemAdapter = null;
        super.onDetach();
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(this, userProfileCacheRx.get(currentUserId.toUserBaseKey()))
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
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                onStopSubscriptions.add(AppObservable.bindFragment(
                        this,
                        userInteractorRx.purchase(ProductIdentifierDomain.values()[productDomainIdentifierOrdinal]))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                Actions.empty(),
                                new ToastOnErrorAction()));
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
            AppObservable.bindFragment(
                    this,
                    userInteractorRx.purchaseAndClear(((StoreItemPromptPurchaseDTO) clickedItem).productIdentifierDomain))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<PurchaseResult>()
                            {
                                @Override public void call(PurchaseResult purchaseResult)
                                {
                                    StoreScreenFragment.this.handlePurchaseFinished(purchaseResult);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable e)
                                {
                                    StoreScreenFragment.this.handlePurchaseFailed(e);
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

    protected void handlePurchaseFinished(@SuppressWarnings("UnusedParameters") @NonNull PurchaseResult purchaseResult)
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
        navigator.get().launchTabActivity(RootFragmentType.ALERTS);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(bundle, currentUserId.toUserBaseKey());
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
