package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.view.View;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBasePurchaseActionInteractor;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THPurchaseActionInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.system.SystemStatusCache;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import timber.log.Timber;

abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".thIntent";

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject protected Provider<THUIBillingRequest> uiBillingRequestProvider;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject protected THBillingInteractor userInteractor;
    @Inject SystemStatusCache systemStatusCache;

    public static void putApplicablePortfolioId(@NotNull Bundle args, @NotNull OwnedPortfolioId ownedPortfolioId)
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

    abstract protected void initViews(View view);

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        portfolioCompactListFetchListener = createPortfolioCompactListFetchListener();
    }

   @Override public void onResume()
    {
        super.onResume();

        fetchPortfolioCompactList();
    }

    @Override public void onStop()
    {
        detachPortfolioCompactListCache();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        portfolioCompactListFetchListener = null;
        super.onDestroy();
    }

    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    private void fetchPortfolioCompactList()
    {
        detachPortfolioCompactListCache();
        portfolioCompactListCache.register(currentUserId.toUserBaseKey(), portfolioCompactListFetchListener);
        portfolioCompactListCache.getOrFetchAsync(currentUserId.toUserBaseKey());
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

    protected THBasePurchaseActionInteractor.Builder createPurchaseActionInteractorBuilder()
    {
        return new THBasePurchaseActionInteractor.Builder()
                .setBillingInteractor(userInteractor)
                .setBillingRequest(uiBillingRequestProvider.get())
                // by default, all are true
                //.startWithProgressDialog(true)
                //.popIfBillingNotAvailable(true)
                //.popIfProductIdentifierFetchFailed(true)
                //.popIfInventoryFetchFailed(true)
                //.popIfPurchaseFailed(true)
                .setPremiumFollowedListener(createPremiumUserFollowedListener())
                .error(new UIBillingRequest.OnErrorListener()
                {
                    @Override public void onError(int requestCode, BillingException billingException)
                    {
                        Timber.e(billingException, "Store had error");
                    }
                });
    }

    //region Following action
    protected final void premiumFollowUser(UserBaseKey heroId)
    {
        THPurchaseActionInteractor thPurchaseActionInteractor = createPurchaseActionInteractorBuilder()
                .setUserToFollow(heroId)
                .setPurchaseApplicableOwnedPortfolioId(purchaseApplicableOwnedPortfolioId)
                .build();

        thPurchaseActionInteractor.premiumFollowUser();
    }

    protected final void unfollowUser(UserBaseKey heroId)
    {
        THPurchaseActionInteractor thPurchaseActionInteractor = createPurchaseActionInteractorBuilder()
                .setUserToFollow(heroId)
                .setPurchaseApplicableOwnedPortfolioId(purchaseApplicableOwnedPortfolioId)
                .build();
        thPurchaseActionInteractor.unfollowUser();
    }
    //endregion

    protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactListFetchListener();
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        protected BasePurchaseManagementPortfolioCompactListFetchListener()
        {
            // no unexpected creation
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    //region Creation and Listener
    @Deprecated
    protected Callback<UserProfileDTO> createFreeUserFollowedCallback()
    {
        // default will be used when this one return null
        return null;
    }

    protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        // default will be used when this one return null
        return null;
    }
    //endregion
}
