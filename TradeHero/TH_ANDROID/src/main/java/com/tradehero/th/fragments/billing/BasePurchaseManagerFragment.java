package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.view.View;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.models.user.MiddleCallbackFollowUser;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * It expects its Activity to implement THIABInteractor.
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".thIntent";

    @Inject protected THBillingInteractor userInteractor;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;
    private Milestone.OnCompleteListener portfolioCompactListRetrievedListener;

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    @Inject protected Provider<THUIBillingRequest> uiBillingRequestProvider;
    @Inject protected com.tradehero.th.billing.googleplay.THIABAlertDialogUtil THIABAlertDialogUtil;
    protected Integer showProductDetailRequestCode;

    protected FollowUserAssistant followUserAssistant;
    protected FollowUserAssistant.OnUserFollowedListener userFollowedListener;

    abstract protected void initViews(View view);

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        portfolioCompactListRetrievedListener = createPortfolioCompactListRetrievedListener();
        userFollowedListener = createUserFollowedListener();
    }

    protected Milestone.OnCompleteListener createPortfolioCompactListRetrievedListener()
    {
        return new BasePurchaseManagementPortfolioCompactListRetrievedListener();
    }

    protected FollowUserAssistant.OnUserFollowedListener createUserFollowedListener()
    {
        return new BasePurchaseManagerUserFollowedListener();
    }

    @Override public void onResume()
    {
        super.onResume();
        prepareApplicableOwnedPortolioId();

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle thIntentBundle = args.getBundle(BUNDLE_KEY_THINTENT_BUNDLE);
            if (thIntentBundle != null)
            {
                int action = thIntentBundle.getInt(THIABBillingInteractor.BUNDLE_KEY_ACTION);
                if (action > 0)
                {
                    userInteractor.doAction(action); // TODO place the action after portfolio has been set
                }
                args.remove(BUNDLE_KEY_THINTENT_BUNDLE);
            }
        }
    }

    @Override public void onDestroyView()
    {
        detachPortfolioRetrievedMilestone();
        detachUserFollowAssistant();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userFollowedListener = null;
        portfolioCompactListRetrievedListener = null;
        super.onDestroy();
    }

    protected void prepareApplicableOwnedPortolioId()
    {
        OwnedPortfolioId applicablePortfolioId = null;

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle portfolioIdBundle = args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
            if (portfolioIdBundle != null)
            {
                applicablePortfolioId = new OwnedPortfolioId(portfolioIdBundle);
            }
        }

        if (applicablePortfolioId == null)
        {
            applicablePortfolioId = new OwnedPortfolioId(currentUserId.get(), null);
        }
        if (applicablePortfolioId.userId == null)
        {
            applicablePortfolioId = new OwnedPortfolioId(currentUserId.get(), applicablePortfolioId.portfolioId);
        }
        if (applicablePortfolioId.portfolioId == null)
        {
            final OwnedPortfolioId ownedPortfolioId = portfolioCompactListCache.getDefaultPortfolio(applicablePortfolioId.getUserBaseKey());
            if (ownedPortfolioId != null && ownedPortfolioId.portfolioId != null)
            {
                applicablePortfolioId = ownedPortfolioId;
            }
            else
            {
                // This situation will be handled by the milestone
            }
        }

        if (applicablePortfolioId.portfolioId == null)
        {
            // At this stage, portfolioId is still null, we need to wait for the fetch
            waitForPortfolioCompactListFetched(applicablePortfolioId.getUserBaseKey());
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

    private void detachPortfolioRetrievedMilestone()
    {
        if (portfolioCompactListRetrievedMilestone != null)
        {
            portfolioCompactListRetrievedMilestone.setOnCompleteListener(null);
        }
        portfolioCompactListRetrievedListener = null;
    }

    private void detachUserFollowAssistant()
    {
        if (followUserAssistant != null)
        {
            followUserAssistant.setUserFollowedListener(null);
        }
        followUserAssistant = null;
    }

    protected void waitForPortfolioCompactListFetched(UserBaseKey userBaseKey)
    {
        detachPortfolioRetrievedMilestone();
        portfolioCompactListRetrievedMilestone = new PortfolioCompactListRetrievedMilestone(userBaseKey);
        portfolioCompactListRetrievedMilestone.setOnCompleteListener(portfolioCompactListRetrievedListener);
        portfolioCompactListRetrievedMilestone.launch();
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
    }

    public void cancelOthersAndShowProductDetailList(ProductIdentifierDomain domain)
    {
        if (showProductDetailRequestCode != null)
        {
            userInteractor.forgetRequestCode(showProductDetailRequestCode);
        }
        showProductDetailRequestCode = showProductDetailListForPurchase(domain);
    }

    public int showProductDetailListForPurchase(ProductIdentifierDomain domain)
    {
        return userInteractor.run(getShowProductDetailRequest(domain));
    }

    public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        THUIBillingRequest request = uiBillingRequestProvider.get();
        request.applicablePortfolioId = getApplicablePortfolioId();
        request.startWithProgressDialog = true;
        request.popIfBillingNotAvailable = true;
        request.popIfProductIdentifierFetchFailed = true;
        request.popIfInventoryFetchFailed = true;
        request.domainToPresent = domain;
        request.popIfPurchaseFailed = true;
        request.onDefaultErrorListener = new UIBillingRequest.OnErrorListener()
        {
            @Override public void onError(int requestCode, BillingException billingException)
            {
                Timber.e(billingException, "Store had error");
            }
        };
        return request;
    }

    public void followUser(UserBaseKey userToFollow)
    {
        detachUserFollowAssistant();
        followUserAssistant = new FollowUserAssistant(userFollowedListener, userToFollow);
        followUserAssistant.launchFollow();
    }

    public void unfollowUser(UserBaseKey userToUnFollow)
    {
        detachUserFollowAssistant();
        followUserAssistant = new FollowUserAssistant(userFollowedListener, userToUnFollow);
        followUserAssistant.launchUnFollow();
    }

    protected class BasePurchaseManagementPortfolioCompactListRetrievedListener implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            prepareApplicableOwnedPortolioId();
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            THToast.show(R.string.error_fetch_portfolio_list_info);
            Timber.e(throwable, "Failed to download portfolio compacts");
        }
    }

    protected class BasePurchaseManagerUserFollowedListener implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override
        public void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO)
        {
            // Children classes should update the display
        }

        @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
        {
            // Anything to do?
        }
    }
}
