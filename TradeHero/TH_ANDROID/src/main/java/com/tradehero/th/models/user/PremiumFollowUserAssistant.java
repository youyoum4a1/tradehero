package com.tradehero.th.models.user;

import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class PremiumFollowUserAssistant extends SimplePremiumFollowUserAssistant
        implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
{
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject Provider<THUIBillingRequest> billingRequestProvider;
    @Inject protected THBillingInteractor billingInteractor;
    protected UserProfileDTO currentUserProfile;
    protected final OwnedPortfolioId applicablePortfolioId;
    @Nullable protected Integer requestCode;

    //<editor-fold desc="Constructors">
    public PremiumFollowUserAssistant(
            @NotNull UserBaseKey userToFollow,
            @Nullable OnUserFollowedListener userFollowedListener,
            OwnedPortfolioId applicablePortfolioId)
    {
        super(userToFollow, userFollowedListener);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override public void launchFollow()
    {
        userProfileCache.register(currentUserId.toUserBaseKey(), this);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
    {
        this.currentUserProfile = value;
        checkBalanceAndFollow();
    }

    @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
    {
        notifyFollowFailed(userToFollow, error);
    }

    protected void checkBalanceAndFollow()
    {
        if (this.currentUserProfile.ccBalance > 0)
        {
            super.launchFollow();
        }
        else
        {
            haveInteractorForget();
            //noinspection unchecked
            requestCode = billingInteractor.run(createPurchaseCCRequest());
        }
    }

    protected void haveInteractorForget()
    {
        if (requestCode != null)
        {
            billingInteractor.forgetRequestCode(requestCode);
        }
        requestCode = null;
    }

    @Override protected void notifyFollowFailed(@NotNull UserBaseKey userToFollow, @NotNull Throwable error)
    {
        haveInteractorForget();
        super.notifyFollowFailed(userToFollow, error);
    }

    @Override protected void notifyFollowSuccess(@NotNull UserBaseKey userToFollow, @NotNull UserProfileDTO currentUserProfile)
    {
        haveInteractorForget();
        super.notifyFollowSuccess(userToFollow, currentUserProfile);
    }

    protected THUIBillingRequest createPurchaseCCRequest()
    {
        THUIBillingRequest billingRequest = billingRequestProvider.get();
        billingRequest.domainToPresent = ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS;
        billingRequest.applicablePortfolioId = applicablePortfolioId;
        billingRequest.userToFollow = userToFollow;
        billingRequest.startWithProgressDialog = true;
        billingRequest.popIfBillingNotAvailable = true;
        billingRequest.popIfProductIdentifierFetchFailed = true;
        billingRequest.popIfInventoryFetchFailed = true;
        billingRequest.popIfPurchaseFailed = true;
        billingRequest.onDefaultErrorListener = new UIBillingRequest.OnErrorListener()
        {
            @Override public void onError(int requestCode, BillingException billingException)
            {
                notifyFollowFailed(userToFollow, billingException);
                Timber.e(billingException, "Store had error");
            }
        };
        billingRequest.purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener()
        {
            @Override
            public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase,
                    UserProfileDTO updatedUserPortfolio)
            {
                PremiumFollowUserAssistant.super.launchFollow();
            }

            @Override
            public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase,
                    BillingException error)
            {
                notifyFollowFailed(userToFollow, error);
                Timber.e(error, "Failed to report purchase");
            }
        };
        return billingRequest;
    }
}
