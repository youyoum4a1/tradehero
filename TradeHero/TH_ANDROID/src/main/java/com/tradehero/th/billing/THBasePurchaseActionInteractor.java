package com.tradehero.th.billing;

import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class THBasePurchaseActionInteractor implements THPurchaseActionInteractor
{
    protected Integer showProductDetailRequestCode;

    private final THBillingInteractor billingInteractor;
    private final THUIBillingRequest billingRequest;
    private final UIBillingRequest.OnErrorListener errorListener;
    private final ProductIdentifierDomain productIdentifierDomain;
    private final PurchaseReporter.OnPurchaseReportedListener purchaseReportedListener;
    private final Callback<UserProfileDTO> freeFollowedListener;

    private final boolean alertsAreFree;
    private final PremiumFollowUserAssistant premiumFollowUserAssistant;

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
    protected THBasePurchaseActionInteractor(THBillingInteractor billingInteractor, THUIBillingRequest billingRequest, UIBillingRequest.OnErrorListener errorListener,
            ProductIdentifierDomain productIdentifierDomain, UserBaseKey userToFollow,
            OwnedPortfolioId purchaseApplicableOwnedPortfolioId,
            PurchaseReporter.OnPurchaseReportedListener purchaseReportedListener, Callback<UserProfileDTO> freeFollowedListener,
            PremiumFollowUserAssistant.OnUserFollowedListener premiumFollowedListener,
            boolean alertsAreFree)
    {
        this.billingInteractor = billingInteractor;
        this.billingRequest = billingRequest;
        this.errorListener = errorListener;
        this.productIdentifierDomain = productIdentifierDomain;
        this.purchaseReportedListener = purchaseReportedListener;
        this.freeFollowedListener = freeFollowedListener;
        this.alertsAreFree = alertsAreFree;

        this.premiumFollowUserAssistant = new PremiumFollowUserAssistant(userToFollow, premiumFollowedListener, purchaseApplicableOwnedPortfolioId);
    }

    @Override public int showProductsList(ProductIdentifierDomain domain)
    {
        detachRequestCode();
        return billingInteractor.run(getShowProductDetailRequest(domain));
    }

    protected THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        billingRequest.domainToPresent = domain;
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
        detachPremiumFollowUserAssistant();
        premiumFollowUserAssistant.launchFollow();
    }

    @Override public void unfollowUser()
    {
        detachPremiumFollowUserAssistant();
        premiumFollowUserAssistant.launchUnFollow();
    }

    private void detachRequestCode()
    {
        if (showProductDetailRequestCode != null && billingInteractor != null)
        {
            billingInteractor.forgetRequestCode(showProductDetailRequestCode);
        }
    }

    /** TODO We might want to remove this. With the old implementation, premiumFollowUserAssistant was keep for multiple purchase actions.
     * This new implementation introduces a immutable way, THPurchaseActionInterfactor is fresh everytime the app need one,
     * so that when the action is finished, everything in this class will be properly collected.
     */
    private void detachPremiumFollowUserAssistant()
    {
        premiumFollowUserAssistant.setUserFollowedListener(null);
    }

    public abstract static class Builder<T extends Builder<T>>
    {
        private THBillingInteractor billingInteractor;
        private PremiumFollowUserAssistant.OnUserFollowedListener premiumFollowedListener;
        private UserBaseKey userToFollow;
        private OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
        private Callback<UserProfileDTO> freeFollowedListener;
        private boolean startWithProgressDialog = true;
        private boolean popIfBillingNotAvailable = true;
        private boolean popIfProductIdentifierFetchFailed = true;
        private boolean popIfInventoryFetchFailed = true;
        private boolean popIfPurchaseFailed = true;
        private UIBillingRequest.OnErrorListener errorListener;
        private ProductIdentifierDomain productIdentifierDomain;
        private PurchaseReporter.OnPurchaseReportedListener purchaseReportedListener;
        private boolean alertsAreFree = alertsAreFree();

        private THUIBillingRequest billingRequest;

        protected abstract T self();

        public Builder setBillingInteractor(THBillingInteractor billingInteractor)
        {
            this.billingInteractor = billingInteractor;
            return self();
        }

        public Builder setUserToFollow(UserBaseKey userToFollow)
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

        public Builder setPremiumFollowedListener(PremiumFollowUserAssistant.OnUserFollowedListener userFollowedListener)
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

        public Builder error(UIBillingRequest.OnErrorListener errorListener)
        {
            this.errorListener = errorListener;
            return self();
        }

        public Builder product(ProductIdentifierDomain productIdentifierDomain)
        {
            this.productIdentifierDomain = productIdentifierDomain;
            return self();
        }

        public Builder setPurchaseReportedListener(PurchaseReporter.OnPurchaseReportedListener purchaseReportedListener)
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
         * @param billingRequest
         * @return
         */
        @Deprecated
        public Builder setBillingRequest(THUIBillingRequest billingRequest)
        {
            this.billingRequest = billingRequest;
            return self();
        }

        public THPurchaseActionInteractor build()
        {
            ensureSaneDefaults();
            populateBillingRequest();
            return new THBasePurchaseActionInteractor(billingInteractor, billingRequest, errorListener,
                    productIdentifierDomain,
                    userToFollow,
                    purchaseApplicableOwnedPortfolioId,
                    purchaseReportedListener,
                    freeFollowedListener,
                    premiumFollowedListener,
                    alertsAreFree);
        }

        // TODO, look at {@link setBillingRequest()}
        private void populateBillingRequest()
        {
            billingRequest.applicablePortfolioId = purchaseApplicableOwnedPortfolioId;
            billingRequest.startWithProgressDialog = startWithProgressDialog;
            billingRequest.popIfBillingNotAvailable = popIfBillingNotAvailable;
            billingRequest.popIfProductIdentifierFetchFailed = popIfProductIdentifierFetchFailed;
            billingRequest.popIfInventoryFetchFailed = popIfInventoryFetchFailed;
            billingRequest.popIfReportFailed = popIfPurchaseFailed;
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

        private static final PremiumFollowUserAssistant.OnUserFollowedListener DEFAULT_PREMIUM_FOLLOWED_LISTENER =
                new PremiumFollowUserAssistant.OnUserFollowedListener()
                {
                    @Override public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
                    {
                        // do something by default?
                    }

                    @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
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
