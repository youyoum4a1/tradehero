package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.tradehero.common.billing.alipay.AlipayActivity;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.models.user.FollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

/**
 * It expects its Activity to implement THIABInteractor. Created with IntelliJ IDEA. User: xavier
 * Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates.
 */
abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE =
            BasePurchaseManagerFragment.class.getName() + ".thIntent";

    @Inject protected THBillingInteractor userInteractor;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;
    private Milestone.OnCompleteListener portfolioCompactListRetrievedListener;

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    @Inject protected Provider<THUIBillingRequest> uiBillingRequestProvider;
    protected Integer showProductDetailRequestCode;

    protected FollowUserAssistant followUserAssistant;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject protected CurrentActivityHolder currentActivityHolder;

    abstract protected void initViews(View view);

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        portfolioCompactListRetrievedListener = createPortfolioCompactListRetrievedListener();
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
                    userInteractor.doAction(
                            action); // TODO place the action after portfolio has been set
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
        portfolioCompactListRetrievedListener = null;
        super.onDestroy();
    }

    protected void prepareApplicableOwnedPortolioId()
    {
        OwnedPortfolioId applicablePortfolioId = null;

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle portfolioIdBundle =
                    args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
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
            applicablePortfolioId =
                    new OwnedPortfolioId(currentUserId.get(), applicablePortfolioId.portfolioId);
        }
        if (applicablePortfolioId.portfolioId == null)
        {
            final OwnedPortfolioId ownedPortfolioId = portfolioCompactListCache.getDefaultPortfolio(
                    applicablePortfolioId.getUserBaseKey());
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

    protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId,
            boolean andDisplay)
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
        portfolioCompactListRetrievedMilestone =
                new PortfolioCompactListRetrievedMilestone(userBaseKey);
        portfolioCompactListRetrievedMilestone.setOnCompleteListener(
                portfolioCompactListRetrievedListener);
        portfolioCompactListRetrievedMilestone.launch();
    }

    public OwnedPortfolioId getApplicablePortfolioId()
    {
        return purchaseApplicableOwnedPortfolioId;
    }

    public void cancelOthersAndShowProductDetailList(ProductIdentifierDomain domain)
    {
        //TODO alipay hardcode
        if (true)
        {
            hackToAlipay(domain);
        }
        else
        {
            if (showProductDetailRequestCode != null)
            {
                userInteractor.forgetRequestCode(showProductDetailRequestCode);
            }
            showProductDetailRequestCode = showProductDetailListForPurchase(domain);
        }
    }

    // HACK Alipay
    public void alipayPopBuy(int type)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(currentActivityHolder.getCurrentActivity());
        int array = 0;
        switch (type)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                array = R.array.alipay_virtual_dollars_array;
                break;
            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                array = R.array.alipay_follow_credits_array;
                break;
            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                array = R.array.alipay_stock_alerts_array;
                break;
            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                array = R.array.alipay_reset_portfolio_array;
                break;
        }
        final int type1 = type;
        builder.setTitle(R.string.app_name)
                .setItems(currentActivityHolder.getCurrentActivity()
                        .getResources()
                        .getStringArray(array),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (checkAlertsPlan(which, type1))
                                {
                                    Intent intent =
                                            new Intent(currentActivityHolder.getCurrentActivity(),
                                                    AlipayActivity.class);
                                    intent.putExtra(AlipayActivity.ALIPAY_TYPE_KEY, type1);
                                    intent.putExtra(AlipayActivity.ALIPAY_POSITION_KEY, which);
                                    currentActivityHolder.getCurrentActivity()
                                            .startActivity(intent);
                                }
                            }
                        });
        builder.create().show();
    }

    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected Lazy<SecurityAlertCountingHelper> securityAlertCountingHelperLazy;

    //TODO refactor
    private boolean checkAlertsPlan(int which, int type1)
    {
        if (type1 != StoreItemAdapter.POSITION_BUY_STOCK_ALERTS)
        {
            return true;
        }
        AlertSlotDTO alertSlots =
                securityAlertCountingHelperLazy.get().getAlertSlots(
                        currentUserId.toUserBaseKey());
        switch (which)
        {
            case 0:
                if (alertSlots.totalAlertSlots >= 2)
                {
                    alertDialogUtilLazy.get()
                            .showDefaultDialog(
                                    currentActivityHolder.getCurrentContext(),
                                    R.string.store_billing_error_buy_alerts);
                    return false;
                }
                break;
            case 1:
                if (alertSlots.totalAlertSlots >= 5)
                {
                    alertDialogUtilLazy.get()
                            .showDefaultDialog(
                                    currentActivityHolder.getCurrentContext(),
                                    R.string.store_billing_error_buy_alerts);
                    return false;
                }
                break;
            case 2:
                break;
        }
        return true;
    }

    public int showProductDetailListForPurchase(ProductIdentifierDomain domain)
    {
        //TODO alipay hardcode
        if (true)
        {
            hackToAlipay(domain);
            return 0;
        }
        return userInteractor.run(getShowProductDetailRequest(domain));
    }

    public void hackToAlipay(ProductIdentifierDomain domain)
    {
        if (domain.equals(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR))
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
        }
        else if (domain.equals(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS))
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS);
        }
        else if (domain.equals(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS))
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_STOCK_ALERTS);
        }
        else if (domain.equals(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO))
        {
            alipayPopBuy(StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO);
        }
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
        followUser(userToFollow, createUserFollowedListener());
    }

    public void followUser(UserBaseKey userToFollow, FollowUserAssistant.OnUserFollowedListener followedListener)
    {
        detachUserFollowAssistant();
        followUserAssistant = new FollowUserAssistant(followedListener, userToFollow, purchaseApplicableOwnedPortfolioId);
        followUserAssistant.launchFollow();
    }

    public void unfollowUser(UserBaseKey userToUnFollow)
    {
        detachUserFollowAssistant();
        followUserAssistant = new FollowUserAssistant(createUserFollowedListener(), userToUnFollow, purchaseApplicableOwnedPortfolioId);
        followUserAssistant.launchUnFollow();
    }

    protected class BasePurchaseManagementPortfolioCompactListRetrievedListener
            implements Milestone.OnCompleteListener
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

    protected class BasePurchaseManagerUserFollowedListener
            implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override
        public void onUserFollowSuccess(UserBaseKey userFollowed,
                UserProfileDTO currentUserProfileDTO)
        {
            // Children classes should update the display
        }

        @Override public void onUserFollowFailed(UserBaseKey userFollowed, Throwable error)
        {
            // Anything to do?
        }
    }
}
