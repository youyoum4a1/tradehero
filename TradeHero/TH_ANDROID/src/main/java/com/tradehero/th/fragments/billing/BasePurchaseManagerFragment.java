package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.tradehero.common.billing.alipay.AlipayActivity;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBasePurchaseActionInteractor;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THPurchaseActionInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import timber.log.Timber;

abstract public class BasePurchaseManagerFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_THINTENT_BUNDLE = BasePurchaseManagerFragment.class.getName() + ".thIntent";

    protected OwnedPortfolioId purchaseApplicableOwnedPortfolioId;
    private DTOCacheNew.Listener<UserBaseKey, PortfolioCompactDTOList> portfolioCompactListFetchListener;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected HeroAlertDialogUtil heroAlertDialogUtil;
    @Inject protected CurrentActivityHolder currentActivityHolder;
    @Inject protected Provider<THUIBillingRequest> uiBillingRequestProvider;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject protected THBillingInteractor userInteractor;
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
    }

    @Override public void onResume()
    {
        super.onResume();

        fetchPortfolioCompactList();
    }

    @Override public void onStop()
    {
        detachPortfolioCompactListCache();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        portfolioCompactListFetchListener = null;
        super.onDestroy();
    }

    private void detachPortfolioCompactListCache()
    {
        portfolioCompactListCache.unregister(portfolioCompactListFetchListener);
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

    protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId,
            boolean andDisplay)
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
//<<<<<<< HEAD
//        //TODO alipay hardcode
//        if (domain.equals(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS) && alertsAreFree())
//        {
//            //hackToAlipay(domain);
//            //now alert is free
//            alertDialogUtil.popWithNegativeButton(getActivity(),
//                    R.string.store_alert_are_free_title, R.string.store_alert_are_free_description,
//                    R.string.ok);
//        }
//        else
//        {
//            detachRequestCode();
//            showProductDetailRequestCode = showProductDetailListForPurchase(domain);
//        }
//    }
//
//    // HACK Alipay
//    public void alipayPopBuy(int type)
//    {
//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(currentActivityHolder.getCurrentActivity());
//        int array = 0;
//        switch (type)
//        {
//            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
//                array = R.array.alipay_virtual_dollars_array;
//                break;
//            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
//                array = R.array.alipay_follow_credits_array;
//                break;
//            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
//                array = R.array.alipay_stock_alerts_array;
//                break;
//            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
//                array = R.array.alipay_reset_portfolio_array;
//                break;
//        }
//        final int type1 = type;
//        builder.setTitle(R.string.app_name)
//                .setItems(currentActivityHolder.getCurrentActivity()
//                        .getResources()
//                        .getStringArray(array),
//                        new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                if (checkAlertsPlan(which, type1))
//                                {
//                                    Intent intent =
//                                            new Intent(currentActivityHolder.getCurrentActivity(),
//                                                    AlipayActivity.class);
//                                    intent.putExtra(AlipayActivity.ALIPAY_TYPE_KEY, type1);
//                                    intent.putExtra(AlipayActivity.ALIPAY_POSITION_KEY, which);
//                                    currentActivityHolder.getCurrentActivity()
//                                            .startActivity(intent);
//                                }
//                            }
//                        });
//        builder.create().show();
//    }
//
//    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
//    @Inject protected Lazy<SecurityAlertCountingHelper> securityAlertCountingHelperLazy;
//
//    //TODO refactor
//    private boolean checkAlertsPlan(int which, int type1)
//    {
//        if (type1 != StoreItemAdapter.POSITION_BUY_STOCK_ALERTS)
//        {
//            return true;
//        }
//        AlertSlotDTO alertSlots =
//                securityAlertCountingHelperLazy.get().getAlertSlots(
//                        currentUserId.toUserBaseKey());
//        switch (which)
//        {
//            case 0:
//                if (alertSlots.totalAlertSlots >= 2)
//                {
//                    alertDialogUtilLazy.get()
//                            .showDefaultDialog(
//                                    currentActivityHolder.getCurrentContext(),
//                                    R.string.store_billing_error_buy_alerts);
//                    return false;
//                }
//                break;
//            case 1:
//                if (alertSlots.totalAlertSlots >= 5)
//                {
//                    alertDialogUtilLazy.get()
//                            .showDefaultDialog(
//                                    currentActivityHolder.getCurrentContext(),
//                                    R.string.store_billing_error_buy_alerts);
//                    return false;
//                }
//                break;
//            case 2:
//                break;
//        }
//        return true;
//    }
//
//    public int showProductDetailListForPurchase(ProductIdentifierDomain domain)
//    {
//        //TODO alipay hardcode
//        if (true)
//        {
//            hackToAlipay(domain);
//            return 0;
//        }
//        return userInteractor.run(getShowProductDetailRequest(domain));
//    }
//
//    public void hackToAlipay(ProductIdentifierDomain domain)
//    {
//        if (domain.equals(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR))
//        {
//            alipayPopBuy(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
//        }
//        else if (domain.equals(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS))
//        {
//            alipayPopBuy(StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS);
//        }
//        else if (domain.equals(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS))
//        {
//            alipayPopBuy(StoreItemAdapter.POSITION_BUY_STOCK_ALERTS);
//        }
//        else if (domain.equals(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO))
//        {
//            alipayPopBuy(StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO);
//        }
//    }
//
//    public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
//    {
//        THUIBillingRequest request = uiBillingRequestProvider.get();
//        request.applicablePortfolioId = getApplicablePortfolioId();
//        request.startWithProgressDialog = true;
//        request.popIfBillingNotAvailable = true;
//        request.popIfProductIdentifierFetchFailed = true;
//        request.popIfInventoryFetchFailed = true;
//        request.domainToPresent = domain;
//        request.popIfPurchaseFailed = true;
//        request.onDefaultErrorListener = new UIBillingRequest.OnErrorListener()
//        {
//            @Override public void onError(int requestCode, BillingException billingException)
//            {
//                Timber.e(billingException, "Store had error");
//            }
//        };
//        return request;
//=======
        return THBasePurchaseActionInteractor.builder()
                .setBillingInteractor(userInteractor)
                .setPurchaseApplicableOwnedPortfolioId(purchaseApplicableOwnedPortfolioId)
                .setBillingRequest(uiBillingRequestProvider.get())
                .startWithProgressDialog(true) // true by default
                .popIfBillingNotAvailable(true)  // true by default
                .popIfProductIdentifierFetchFailed(true) // true by default
                .popIfInventoryFetchFailed(true) // true by default
                .popIfPurchaseFailed(true) // true by default
                .setPremiumFollowedListener(createPremiumUserFollowedListener())
                .error(new UIBillingRequest.OnErrorListener()
                {
                    @Override public void onError(int requestCode, BillingException billingException)
                    {
                        Timber.e(billingException, "Store had error");
                    }
                });
//>>>>>>> origin/develop2.0
    }

    // region Following action
    // should call this method where the action takes place
    @Deprecated
    protected final void premiumFollowUser(@NotNull UserBaseKey heroId)
    {
        THPurchaseActionInteractor thPurchaseActionInteractor = createPurchaseActionInteractorBuilder()
                .setUserToFollow(heroId)
                .setPurchaseApplicableOwnedPortfolioId(purchaseApplicableOwnedPortfolioId)
                .build();

        thPurchaseActionInteractor.premiumFollowUser();
    }

    // should call it where the action takes place
    @Deprecated
    protected final void unfollowUser(@NotNull UserBaseKey heroId)
    {
        THPurchaseActionInteractor thPurchaseActionInteractor = createPurchaseActionInteractorBuilder()
                .setUserToFollow(heroId)
                .setPurchaseApplicableOwnedPortfolioId(purchaseApplicableOwnedPortfolioId)
                .build();
        thPurchaseActionInteractor.unfollowUser();
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

    protected PremiumFollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        // default will be used when this one return null
        return null;
    }
    //endregion
}
