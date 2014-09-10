package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.view.View;

import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.BaseUIBillingRequest;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBasePurchaseActionInteractor;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THPurchaseActionInteractor;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.system.SystemStatusCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;

import retrofit.Callback;
import timber.log.Timber;

abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".thIntent";

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;
    protected THPurchaseActionInteractor thPurchaseActionInteractor;
    protected Integer requestCode;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected THBillingInteractor userInteractor;
    @Inject protected Provider<BaseTHUIBillingRequest.Builder> uiBillingRequestBuilderProvider;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
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
        purchaseApplicableOwnedPortfolioId = getApplicablePortfolioId(getArguments());
    }

    @Override public void onResume()
    {
        super.onResume();

        fetchPortfolioCompactList();
    }

    @Override public void onStop()
    {
        detachRequestCode();
        detachPortfolioCompactListCache();
        detachPurchaseActionInteractor();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        portfolioCompactListFetchListener = null;
        super.onDestroy();
    }

    protected void detachRequestCode()
    {
        if (requestCode != null)
        {
            userInteractor.forgetRequestCode(requestCode);
        }
        requestCode = null;
    }

    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    private void detachPurchaseActionInteractor()
    {
        if (thPurchaseActionInteractor != null)
        {
            thPurchaseActionInteractor.onDestroy();
        }
        thPurchaseActionInteractor = null;
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
        return THBasePurchaseActionInteractor.builder()
                .setBillingInteractor(userInteractor)
                .setPurchaseApplicableOwnedPortfolioId(purchaseApplicableOwnedPortfolioId)
                .setBillingRequestBuilder(uiBillingRequestBuilderProvider.get())
                .startWithProgressDialog(true) // true by default
                .popIfBillingNotAvailable(true)  // true by default
                .popIfProductIdentifierFetchFailed(true) // true by default
                .popIfInventoryFetchFailed(true) // true by default
                .popIfPurchaseFailed(true) // true by default
                .setPremiumFollowedListener(createPremiumUserFollowedListener())
                .error(new BaseUIBillingRequest.OnErrorListener()
                {
                    @Override public void onError(int requestCode, BillingException billingException)
                    {
                        Timber.e(billingException, "Store had error");
                    }
                });
    }

    // region Following action
    // should call this method where the action takes place
    @Deprecated
    protected final void premiumFollowUser(@NotNull UserBaseKey heroId)
    {
        detachRequestCode();
        //noinspection unchecked
        THUIBillingRequest uiRequest = (THUIBillingRequest) uiBillingRequestBuilderProvider.get()
                    .domainToPresent(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS)
                    .applicablePortfolioId(purchaseApplicableOwnedPortfolioId)
                    .userToPremiumFollow(heroId)
                    .purchaseReportedListener(createPurchaseReportedListener())
                    .doPurchase(true)
                    .build();
        //noinspection unchecked
        requestCode = userInteractor.run(uiRequest);
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

    protected THPurchaseReporter.OnPurchaseReportedListener createPurchaseReportedListener()
    {
        return null;
    }

    // Use createPurchaseReportedListener
    @Deprecated protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        // default will be used when this one return null
        return null;
    }
    //endregion
}
