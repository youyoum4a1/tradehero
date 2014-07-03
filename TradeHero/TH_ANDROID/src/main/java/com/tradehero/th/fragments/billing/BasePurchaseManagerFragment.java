package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.view.View;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.system.SystemStatusCache;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".thIntent";

    @Inject protected THBillingInteractor userInteractor;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected SystemStatusCache systemStatusCache;
    private DTOCacheNew.Listener<UserBaseKey, SystemStatusDTO> systemStatusCacheListener;
    protected SystemStatusDTO systemStatusDTO;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;
    private Milestone.OnCompleteListener portfolioCompactListRetrievedListener;

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    @Inject protected Provider<THUIBillingRequest> uiBillingRequestProvider;
    protected Integer showProductDetailRequestCode;

    protected PremiumFollowUserAssistant premiumFollowUserAssistant;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;

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
        portfolioCompactListRetrievedListener = createPortfolioCompactListRetrievedListener();
        systemStatusCacheListener = createSystemStatusCacheListener();
    }

    protected Milestone.OnCompleteListener createPortfolioCompactListRetrievedListener()
    {
        return new BasePurchaseManagementPortfolioCompactListRetrievedListener();
    }

    protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new BasePurchaseManagerPremiumUserFollowedListener();
    }

    protected Callback<UserProfileDTO> createFreeUserFollowedCallback()
    {
        return new BasePurchaseManagerFreeUserFollowedCallback();
    }

    @Override public void onStart()
    {
        super.onStart();
        systemStatusCache.register(currentUserId.toUserBaseKey(), systemStatusCacheListener);
        systemStatusCache.getOrFetchAsync(currentUserId.toUserBaseKey());
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

    @Override public void onStop()
    {
        detachSystemStatusCache();
        detachPortfolioRetrievedMilestone();
        detachPremiumFollowUserAssistant();
        detachRequestCode();
        super.onStop();
    }

    protected void detachRequestCode()
    {
        if (showProductDetailRequestCode != null && userInteractor != null)
        {
            userInteractor.forgetRequestCode(showProductDetailRequestCode);
        }
    }

    @Override public void onDestroy()
    {
        systemStatusCacheListener = null;
        portfolioCompactListRetrievedListener = null;
        super.onDestroy();
    }

    protected void detachSystemStatusCache()
    {
        systemStatusCache.unregister(systemStatusCacheListener);
    }

    protected void prepareApplicableOwnedPortolioId()
    {
        Bundle args = getArguments();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId(args);

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

    private void detachPremiumFollowUserAssistant()
    {
        if (premiumFollowUserAssistant != null)
        {
            premiumFollowUserAssistant.setUserFollowedListener(null);
        }
        premiumFollowUserAssistant = null;
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

    protected void passApplicablePortfolioId(@NotNull Bundle args)
    {
        if(getApplicablePortfolioId() != null)
        {
            BasePurchaseManagerFragment.putApplicablePortfolioId(args, getApplicablePortfolioId());
        }
    }

    protected boolean alertsAreFree()
    {
        return systemStatusDTO != null && systemStatusDTO.alertsAreFree;
    }

    public void cancelOthersAndShowProductDetailList(ProductIdentifierDomain domain)
    {
        if (domain.equals(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS) && alertsAreFree())
        {
            alertDialogUtil.popWithNegativeButton(
                    getActivity(),
                    R.string.store_alert_are_free_title,
                    R.string.store_alert_are_free_description,
                    R.string.ok);
        }
        else
        {
            detachRequestCode();
            showProductDetailRequestCode = showProductDetailListForPurchase(domain);
        }
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

    public void premiumFollowUser(UserBaseKey heroId)
    {
        premiumFollowUser(heroId, createPremiumUserFollowedListener());
    }

    public void premiumFollowUser(UserBaseKey heroId,
            PremiumFollowUserAssistant.OnUserFollowedListener followedListener)
    {
        detachPremiumFollowUserAssistant();
        premiumFollowUserAssistant = new PremiumFollowUserAssistant(followedListener, heroId, purchaseApplicableOwnedPortfolioId);
        premiumFollowUserAssistant.launchFollow();
    }

    public void unfollowUser(UserBaseKey heroId)
    {
        detachPremiumFollowUserAssistant();
        premiumFollowUserAssistant = new PremiumFollowUserAssistant(
                createPremiumUserFollowedListener(), heroId, purchaseApplicableOwnedPortfolioId);
        premiumFollowUserAssistant.launchUnFollow();
    }

    protected DTOCacheNew.Listener<UserBaseKey, SystemStatusDTO> createSystemStatusCacheListener()
    {
        return new BasePurchaseManagementSystemStatusCacheListener();
    }

    protected class BasePurchaseManagementSystemStatusCacheListener implements DTOCacheNew.Listener<UserBaseKey, SystemStatusDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, SystemStatusDTO value)
        {
            BasePurchaseManagerFragment.this.systemStatusDTO = value;
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
        }
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

    protected class BasePurchaseManagerFreeUserFollowedCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            // Children classes should update the display
        }

        @Override public void failure(RetrofitError error)
        {
            // Anything to do?
        }
    }

    protected class BasePurchaseManagerPremiumUserFollowedListener implements PremiumFollowUserAssistant.OnUserFollowedListener
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
