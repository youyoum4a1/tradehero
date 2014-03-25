package com.tradehero.th.models.user;

import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by xavier on 3/24/14.
 */
public class FollowUserAssistant implements
        Callback<UserProfileDTO>,
        DTOCache.Listener<UserBaseKey, UserProfileDTO>
{
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    protected UserProfileDTO currentUserProfile;
    @Inject protected UserServiceWrapper userServiceWrapper;
    protected final UserBaseKey userToFollow;
    protected final OwnedPortfolioId applicablePortfolioId;
    @Inject protected THBillingInteractor billingInteractor;
    @Inject Provider<THUIBillingRequest> billingRequestProvider;
    private OnUserFollowedListener userFollowedListener;
    protected Integer requestCode;

    public FollowUserAssistant(OnUserFollowedListener userFollowedListener, UserBaseKey userToFollow, OwnedPortfolioId applicablePortfolioId)
    {
        this.userFollowedListener = userFollowedListener;
        this.userToFollow = userToFollow;
        this.applicablePortfolioId = applicablePortfolioId;
        DaggerUtils.inject(this);
    }

    public void launchFollow()
    {
        userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), this).execute();
    }

    public void launchUnFollow()
    {
        unFollow();
    }

    public void setUserFollowedListener(OnUserFollowedListener userFollowedListener)
    {
        this.userFollowedListener = userFollowedListener;
    }

    @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
    {
        this.currentUserProfile = value;
        follow();
    }

    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
    {
        notifyFollowFailed(key, error);
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        notifyFollowSuccess(userToFollow, userProfileDTO);
    }

    @Override public void failure(RetrofitError error)
    {
        notifyFollowFailed(userToFollow, error);
    }

    protected void notifyFollowSuccess(UserBaseKey userToFollow, UserProfileDTO currentUserProfile)
    {
        haveInteractorForget();
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowSuccess(userToFollow, currentUserProfile);
        }
    }

    protected void notifyFollowFailed(UserBaseKey userToFollow, Throwable error)
    {
        haveInteractorForget();
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowFailed(userToFollow, error);
        }
    }

    protected void haveInteractorForget()
    {
        if (requestCode != null)
        {
            billingInteractor.forgetRequestCode(requestCode);
        }
        requestCode =  null;
    }

    protected void follow()
    {
        if (this.currentUserProfile.ccBalance > 0)
        {
            userServiceWrapper.follow(userToFollow, this);
        }
        else
        {
            haveInteractorForget();
            requestCode = billingInteractor.run(createPurchaseCCRequest());
        }
    }

    protected void unFollow()
    {
        userServiceWrapper.unfollow(userToFollow, this);
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
            public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                notifyFollowSuccess(userToFollow, updatedUserPortfolio);
            }

            @Override
            public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
            {
                notifyFollowFailed(userToFollow, error);
                Timber.e(error, "Failed to report purchase");
            }
        };
        return billingRequest;
    }

    public static interface OnUserFollowedListener
    {
        void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO);
        void onUserFollowFailed(UserBaseKey userFollowed, Throwable error);
    }
}
