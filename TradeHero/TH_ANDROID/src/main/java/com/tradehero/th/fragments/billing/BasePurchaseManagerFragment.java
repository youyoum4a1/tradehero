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
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
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
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

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
        portfolioCompactListFetchListener = createPortfolioCompactListFetchListener();
        systemStatusCacheListener = createSystemStatusCacheListener();
    }

    protected DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> createPortfolioCompactListFetchListener()
    {
        return new BasePurchaseManagementPortfolioCompactListFetchListener();
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

        fetchPortfolioCompactList();
    }

    @Override public void onStop()
    {
        detachSystemStatusCache();
        detachPortfolioCompactListCache();
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
        portfolioCompactListFetchListener = null;
        super.onDestroy();
    }

    protected void detachSystemStatusCache()
    {
        systemStatusCache.unregister(systemStatusCacheListener);
    }

    protected void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
    }

    protected void fetchPortfolioCompactList()
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
        doActionFromIntent();
        if (andDisplay)
        {
        }
    }

    protected void doActionFromIntent()
    {
        Bundle args = getArguments();
        if (args != null)
        {
            Bundle thIntentBundle = args.getBundle(BUNDLE_KEY_THINTENT_BUNDLE);
            if (thIntentBundle != null)
            {
                int action = thIntentBundle.getInt(THIABBillingInteractor.BUNDLE_KEY_ACTION);
                if (action > 0)
                {
                    userInteractor.doAction(action);
                }
                args.remove(BUNDLE_KEY_THINTENT_BUNDLE);
            }
        }
    }

    private void detachPremiumFollowUserAssistant()
    {
        if (premiumFollowUserAssistant != null)
        {
            premiumFollowUserAssistant.setUserFollowedListener(null);
        }
        premiumFollowUserAssistant = null;
    }

    @Nullable public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
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
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull SystemStatusDTO value)
        {
            BasePurchaseManagerFragment.this.systemStatusDTO = value;
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
        }
    }

    protected class BasePurchaseManagementPortfolioCompactListFetchListener implements DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull PortfolioCompactDTOList value)
        {
            prepareApplicableOwnedPortolioId(value.getDefaultPortfolio());
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_portfolio_list_info);
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
