package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".thIntent";

    @Nullable protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    protected Integer requestCode;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected THBillingInteractorRx userInteractorRx;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;

    protected Observable<PortfolioCompactDTOList> currentUserPortfolioCompactListObservable;
    @Nullable protected Subscription portfolioCompactListCacheSubscription;

    public static void putApplicablePortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    public static OwnedPortfolioId getApplicablePortfolioId(@Nullable Bundle args)
    {
        if (args != null)
        {
            if (args.containsKey(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE))
            {
                return new OwnedPortfolioId(args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE));
            }
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());
        currentUserPortfolioCompactListObservable = portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                        .map(pair -> pair.second)
                        .publish()
                        .refCount()
                        .cache(1);
    }

    @Override public void onResume()
    {
        super.onResume();
        softFetchPortfolioCompactList();
    }

    @Override public void onStop()
    {
        unsubscribe(portfolioCompactListCacheSubscription);
        portfolioCompactListCacheSubscription = null;
        super.onStop();
    }

    @Override public void onDestroy()
    {
        currentUserPortfolioCompactListObservable = null;
        super.onDestroy();
    }

    protected void softFetchPortfolioCompactList()
    {
        PortfolioCompactDTOList list = portfolioCompactListCache.getValue(currentUserId.toUserBaseKey());
        if (list == null)
        {
            fetchPortfolioCompactList();
        }
        else
        {
            handleReceivedPortfolioCompactList(list);
        }
    }

    protected void fetchPortfolioCompactList()
    {
        unsubscribe(portfolioCompactListCacheSubscription);
        portfolioCompactListCacheSubscription = AndroidObservable.bindFragment(
                this,
                currentUserPortfolioCompactListObservable)
                .subscribe(
                        this::handleReceivedPortfolioCompactList,
                        e -> {
                            Timber.e(e, "Failed fetching portfolios list");
                            THToast.show(R.string.error_fetch_portfolio_list_info);
                        });
    }

    protected void handleReceivedPortfolioCompactList(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        prepareApplicableOwnedPortolioId(getPreferredApplicablePortfolio(portfolioCompactDTOs));
    }

    @Nullable protected PortfolioCompactDTO getPreferredApplicablePortfolio(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return portfolioCompactDTOs.getDefaultPortfolio();
    }

    protected void prepareApplicableOwnedPortolioId(@Nullable PortfolioCompactDTO defaultIfNotInArgs)
    {
        Bundle args = getArguments();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId(args);

        if (applicablePortfolioId == null && defaultIfNotInArgs != null)
        {
            applicablePortfolioId = defaultIfNotInArgs.getOwnedPortfolioId();
        }

        if (applicablePortfolioId == null)
        {
            Timber.e(new NullPointerException(), "Null applicablePortfolio");
        }
        else
        {
            linkWithApplicable(applicablePortfolioId, true);
        }
    }

    protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId, boolean andDisplay)
    {
        this.purchaseApplicableOwnedPortfolioId = purchaseApplicablePortfolioId;
        if (andDisplay)
        {
        }
    }

    @Nullable public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
    }
}
