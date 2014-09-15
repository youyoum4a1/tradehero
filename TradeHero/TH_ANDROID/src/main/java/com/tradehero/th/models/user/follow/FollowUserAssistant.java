package com.tradehero.th.models.user.follow;

import android.content.Context;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THPurchaseReporter;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class FollowUserAssistant extends SimpleFollowUserAssistant
        implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
{
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject Provider<BaseTHUIBillingRequest.Builder> billingRequestBuilderProvider;
    @Inject protected THBillingInteractor billingInteractor;

    protected UserProfileDTO currentUserProfile;
    @NotNull protected final OwnedPortfolioId applicablePortfolioId;
    @Nullable protected Integer requestCode;

    //<editor-fold desc="Constructors">
    public FollowUserAssistant(
            @NotNull Context context,
            @NotNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        super(context, heroId, userFollowedListener);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        haveInteractorForget();
        userProfileCache.unregister(this);
        super.onDestroy();
    }

    @Override public void launchPremiumFollow()
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
        notifyFollowFailed(heroId, error);
    }

    protected void checkBalanceAndFollow()
    {
        if (this.currentUserProfile.ccBalance > 0)
        {
            super.launchPremiumFollow();
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
        BaseTHUIBillingRequest.Builder builder = billingRequestBuilderProvider.get();
        builder.domainToPresent(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
        builder.applicablePortfolioId(applicablePortfolioId);
        builder.userToPremiumFollow(heroId);
        builder.startWithProgressDialog(true);
        builder.popIfBillingNotAvailable(true);
        builder.popIfProductIdentifierFetchFailed(true);
        builder.popIfInventoryFetchFailed(true);
        builder.popIfPurchaseFailed(true);
        //noinspection unchecked
        builder.purchaseReportedListener(new PremiumFollowPurchaseReportedListener());
        return builder.build();
    }

    protected class PremiumFollowPurchaseReportedListener implements THPurchaseReporter.OnPurchaseReportedListener
    {
        @Override
        public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase,
                UserProfileDTO updatedUserPortfolio)
        {
            if (updatedUserPortfolio.isPremiumFollowingUser(heroId))
            {
                notifyFollowSuccess(heroId, updatedUserPortfolio);
            }
            else // Just covering the case where the interactor did not follow with the purchase
            {
                FollowUserAssistant.super.launchPremiumFollow();
            }
        }

        @Override
        public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase,
                BillingException error)
        {
            notifyFollowFailed(heroId, error);
            Timber.e(error, "Failed to report purchase");
        }
    }
}
