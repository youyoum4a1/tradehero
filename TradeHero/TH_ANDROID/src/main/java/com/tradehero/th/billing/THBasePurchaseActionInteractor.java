package com.tradehero.th.billing;

import com.tradehero.common.billing.request.BaseUIBillingRequest;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class THBasePurchaseActionInteractor implements THPurchaseActionInteractor
{
    protected Integer showProductDetailRequestCode;

    private final THBillingInteractor billingInteractor;
    private final THUIBillingRequest billingRequest;
    private BaseUIBillingRequest.OnErrorListener errorListener;
    private final ProductIdentifierDomain productIdentifierDomain;
    private THPurchaseReporter.OnPurchaseReportedListener purchaseReportedListener;
    private Callback<UserProfileDTO> freeFollowedListener;

    private final boolean alertsAreFree;
    @Nullable private final FollowUserAssistant premiumFollowUserAssistant;

    /**
     * Convenient class that will be used to do any purchasing action such as buying extra cash, follow credits, reset portfolio...),
     * or any action related to purchasing
     *
     * TODO should create an interface for freeFollowedListener instead of using Callback interface from retrofit
     * @param billingInteractor
     * @param billingRequest
     * @param errorListener
     * @param productIdentifierDomain
     * @param userToFollow user to follow
     * @param purchaseApplicableOwnedPortfolioId PortfolioId for making billing request, see {@link .applicablePortfolioId(com.tradehero.common
* .billing.request.UIBillingRequest)}
     * @param purchaseReportedListener
     * @param freeFollowedListener listener to be notified when free follow action is completed
     * @param premiumFollowedListener listener to be notified when premium follow action is completed
     * @param alertsAreFree
     */
    protected THBasePurchaseActionInteractor(
            THBillingInteractor billingInteractor,
            THUIBillingRequest billingRequest,
            BaseUIBillingRequest.OnErrorListener errorListener,
            ProductIdentifierDomain productIdentifierDomain,
            @Nullable UserBaseKey userToFollow,
            OwnedPortfolioId purchaseApplicableOwnedPortfolioId,
            THPurchaseReporter.OnPurchaseReportedListener purchaseReportedListener,
            Callback<UserProfileDTO> freeFollowedListener,
            FollowUserAssistant.OnUserFollowedListener premiumFollowedListener,
            boolean alertsAreFree)
    {
        this.billingInteractor = billingInteractor;
        this.billingRequest = billingRequest;
        this.errorListener = errorListener;
        this.productIdentifierDomain = productIdentifierDomain;
        this.purchaseReportedListener = purchaseReportedListener;
        this.freeFollowedListener = freeFollowedListener;
        this.alertsAreFree = alertsAreFree;

        if (userToFollow != null)
        {
            this.premiumFollowUserAssistant = new FollowUserAssistant(userToFollow, premiumFollowedListener, purchaseApplicableOwnedPortfolioId);
        }
        else
        {
            this.premiumFollowUserAssistant = null;
        }
    }

    public void onDestroy()
    {
        if (billingRequest != null)
        {
            billingRequest.onDestroy();
        }
        if (billingInteractor != null)
        {
            billingInteractor.onDestroy();
        }
        errorListener = null;
        purchaseReportedListener = null;
        freeFollowedListener = null;
        if (premiumFollowUserAssistant != null)
        {
            premiumFollowUserAssistant.onDestroy();
        }
    }

    @Override public int showProductsList(ProductIdentifierDomain domain)
    {
        detachRequestCode();
        //noinspection unchecked
        return billingInteractor.run(getShowProductDetailRequest(domain));
    }

    protected THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        billingRequest.setDomainToPresent(domain);
        return billingRequest;
    }

    @Override public int buyVirtualDollar()
    {
        return showProductsList(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR);
    }

    @Override public int buyFollowCredits()
    {
        return showProductsList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
    }

    @Override public int buyStockAlertSubscription()
    {
        return showProductsList(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS);
    }

    @Override public int resetPortfolio()
    {
        return showProductsList(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO);
    }

    @Override public void premiumFollowUser()
    {
        // Do not call if no hero to follow
        //noinspection ConstantConditions
        premiumFollowUserAssistant.launchPremiumFollow();
    }

    @Override public void unfollowUser()
    {
        // Do not call if no hero to follow
        //noinspection ConstantConditions
        premiumFollowUserAssistant.launchUnFollow();
    }

    private void detachRequestCode()
    {
        if (showProductDetailRequestCode != null && billingInteractor != null)
        {
            billingInteractor.forgetRequestCode(showProductDetailRequestCode);
        }
    }

    public abstract static class Builder<T extends Builder<T>>
    {
        private THBillingInteractor billingInteractor;
        private FollowUserAssistant.OnUserFollowedListener premiumFollowedListener;
        @Nullable private UserBaseKey userToFollow;
        private OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
        private Callback<UserProfileDTO> freeFollowedListener;
        private boolean startWithProgressDialog = true;
        private boolean popIfBillingNotAvailable = true;
        private boolean popIfProductIdentifierFetchFailed = true;
        private boolean popIfInventoryFetchFailed = true;
        private boolean popIfPurchaseFailed = true;
        private BaseUIBillingRequest.OnErrorListener errorListener;
        private ProductIdentifierDomain productIdentifierDomain;
        private THPurchaseReporter.OnPurchaseReportedListener purchaseReportedListener;
        private boolean alertsAreFree = alertsAreFree();

        private BaseTHUIBillingRequest.Builder billingRequestBuilder;

        protected abstract T self();

        public Builder setBillingInteractor(THBillingInteractor billingInteractor)
        {
            this.billingInteractor = billingInteractor;
            return self();
        }

        public Builder setUserToFollow(@NotNull UserBaseKey userToFollow)
        {
            this.userToFollow = userToFollow;
            return self();
        }

        public Builder setPurchaseApplicableOwnedPortfolioId(OwnedPortfolioId purchaseApplicableOwnedPortfolioId)
        {
            this.purchaseApplicableOwnedPortfolioId = purchaseApplicableOwnedPortfolioId;
            return self();
        }

        public Builder setFreeFollowedListener(Callback<UserProfileDTO> freeFollowedListener)
        {
            this.freeFollowedListener = freeFollowedListener;
            return self();
        }

        public Builder setPremiumFollowedListener(FollowUserAssistant.OnUserFollowedListener userFollowedListener)
        {
            this.premiumFollowedListener = userFollowedListener;
            return self();
        }

        public Builder startWithProgressDialog(boolean startWithProgressDialog)
        {
            this.startWithProgressDialog = startWithProgressDialog;
            return self();
        }

        public Builder popIfBillingNotAvailable(boolean popIfBillingNotAvailable)
        {
            this.popIfBillingNotAvailable = popIfBillingNotAvailable;
            return self();
        }

        public Builder popIfProductIdentifierFetchFailed(boolean popIfProductIdentifierFetchFailed)
        {
            this.popIfProductIdentifierFetchFailed = popIfProductIdentifierFetchFailed;
            return self();
        }

        public Builder popIfInventoryFetchFailed(boolean popIfInventoryFetchFailed)
        {
            this.popIfInventoryFetchFailed = popIfInventoryFetchFailed;
            return self();
        }

        public Builder popIfPurchaseFailed(boolean popIfPurchaseFailed)
        {
            this.popIfPurchaseFailed = popIfPurchaseFailed;
            return self();
        }

        public Builder error(BaseUIBillingRequest.OnErrorListener errorListener)
        {
            this.errorListener = errorListener;
            return self();
        }

        public Builder product(ProductIdentifierDomain productIdentifierDomain)
        {
            this.productIdentifierDomain = productIdentifierDomain;
            return self();
        }

        public Builder setPurchaseReportedListener(THPurchaseReporter.OnPurchaseReportedListener purchaseReportedListener)
        {
            this.purchaseReportedListener = purchaseReportedListener;
            return self();
        }

        public Builder setAlertsFree(boolean isAlertsFree)
        {
            this.alertsAreFree = isAlertsFree;
            return self();
        }

        /**
         * We should create billingRequest from other properties instead of setting it here
         * This is for the time being ...
         * @param billingRequestBuilder
         * @return
         */
        @Deprecated
        public Builder setBillingRequestBuilder(BaseTHUIBillingRequest.Builder billingRequestBuilder)
        {
            this.billingRequestBuilder = billingRequestBuilder;
            return self();
        }

        public THPurchaseActionInteractor build()
        {
            ensureSaneDefaults();
            populateBillingRequestBuilder();
            return new THBasePurchaseActionInteractor(
                    billingInteractor,
                    billingRequestBuilder.build(),
                    errorListener,
                    productIdentifierDomain,
                    userToFollow,
                    purchaseApplicableOwnedPortfolioId,
                    purchaseReportedListener,
                    freeFollowedListener,
                    premiumFollowedListener,
                    alertsAreFree);
        }

        // TODO, look at {@link setBillingRequest()}
        private void populateBillingRequestBuilder()
        {
            billingRequestBuilder.applicablePortfolioId(purchaseApplicableOwnedPortfolioId);
            billingRequestBuilder.startWithProgressDialog(startWithProgressDialog);
            billingRequestBuilder.popIfBillingNotAvailable(popIfBillingNotAvailable);
            billingRequestBuilder.popIfProductIdentifierFetchFailed(popIfProductIdentifierFetchFailed);
            billingRequestBuilder.popIfInventoryFetchFailed(popIfInventoryFetchFailed);
            billingRequestBuilder.popIfReportFailed(popIfPurchaseFailed);
        }

        private void ensureSaneDefaults()
        {
            if (premiumFollowedListener == null)
            {
                premiumFollowedListener = DEFAULT_PREMIUM_FOLLOWED_LISTENER;
            }

            if (freeFollowedListener == null)
            {
                freeFollowedListener = DEFAULT_FREE_FOLLOWED_LISTENER;
            }
        }

        /** We assume that this function is called only when systemStatusDTO is available in the cache. systemStatusDTO is requested on
         * DashboardActivity started, so that it is available in very early stage
         */
        protected final boolean alertsAreFree()
        {
            //SystemStatusDTO systemStatusDTO = systemStatusCache.get(currentUserId.toUserBaseKey());
            //return systemStatusDTO != null && systemStatusDTO.alertsAreFree;
            return true;
        }

        private static final Callback<UserProfileDTO> DEFAULT_FREE_FOLLOWED_LISTENER =
                new Callback<UserProfileDTO>()
                {
                    @Override public void success(UserProfileDTO userProfileDTO, Response response)
                    {
                        // Children classes should update the display
                    }

                    @Override public void failure(RetrofitError error)
                    {
                        // Anything to do?
                    }
                };

        private static final FollowUserAssistant.OnUserFollowedListener DEFAULT_PREMIUM_FOLLOWED_LISTENER =
                new FollowUserAssistant.OnUserFollowedListener()
                {
                    @Override public void onUserFollowSuccess(@NotNull UserBaseKey userFollowed, @NotNull UserProfileDTO currentUserProfileDTO)
                    {
                        // do something by default?
                    }

                    @Override public void onUserFollowFailed(@NotNull UserBaseKey userFollowed, @NotNull Throwable error)
                    {
                        // do something by default?
                    }
                };
    }

    private static class Builder2 extends Builder<Builder2>
    {

        @Override protected Builder2 self()
        {
            return this;
        }
    }

    public static Builder<?> builder() {
        return new Builder2();
    }
}
