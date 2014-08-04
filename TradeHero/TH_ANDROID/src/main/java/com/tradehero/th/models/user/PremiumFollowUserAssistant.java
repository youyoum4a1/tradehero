package com.tradehero.th.models.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.alipay.AlipayActivity;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.StoreItemAdapter;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.client.Response;
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
    @Inject protected Lazy<HeroListCache> heroListCacheLazy;
    @Inject protected Lazy<SecurityAlertCountingHelper> securityAlertCountingHelperLazy;
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
            //TODO alipay hardcode
            if (true)
            {
                hackToAlipay(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
            }
            else
            {
                haveInteractorForget();
                //noinspection unchecked
                requestCode = billingInteractor.run(createPurchaseCCRequest());
            }
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

    // HACK Alipay
    public void alipayPopBuy(int type)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(currentActivityHolderLazy.get().getCurrentActivity());
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
                .setItems(currentActivityHolderLazy.get().getCurrentActivity()
                        .getResources()
                        .getStringArray(array),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (checkAlertsPlan(which, type1))
                                {
                                    Intent intent =
                                            new Intent(currentActivityHolderLazy.get().getCurrentActivity(),
                                                    AlipayActivity.class);
                                    intent.putExtra(AlipayActivity.ALIPAY_TYPE_KEY, type1);
                                    intent.putExtra(AlipayActivity.ALIPAY_POSITION_KEY, which);
                                    currentActivityHolderLazy.get().getCurrentActivity()
                                            .startActivity(intent);
                                }
                            }
                        });
        builder.create().show();
    }

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
                                    currentActivityHolderLazy.get().getCurrentContext(),
                                    R.string.store_billing_error_buy_alerts);
                    return false;
                }
                break;
            case 1:
                if (alertSlots.totalAlertSlots >= 5)
                {
                    alertDialogUtilLazy.get()
                            .showDefaultDialog(
                                    currentActivityHolderLazy.get().getCurrentContext(),
                                    R.string.store_billing_error_buy_alerts);
                    return false;
                }
                break;
            case 2:
                break;
        }
        return true;
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
