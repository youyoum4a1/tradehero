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
import com.tradehero.th.activities.CurrentActivityHolder;
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
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class PremiumFollowUserAssistant implements
        Callback<UserProfileDTO>,
        DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
{
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    protected UserProfileDTO currentUserProfile;
    @Inject protected UserServiceWrapper userServiceWrapper;
    protected final UserBaseKey userToFollow;
    protected final OwnedPortfolioId applicablePortfolioId;
    @Inject protected THBillingInteractor billingInteractor;
    @Inject Provider<THUIBillingRequest> billingRequestProvider;
    @Inject protected Lazy<CurrentActivityHolder> currentActivityHolderLazy;
    @Inject protected Lazy<HeroListCache> heroListCacheLazy;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected Lazy<SecurityAlertCountingHelper> securityAlertCountingHelperLazy;
    private OnUserFollowedListener userFollowedListener;
    protected Integer requestCode;

    public PremiumFollowUserAssistant(OnUserFollowedListener userFollowedListener,
            UserBaseKey userToFollow, OwnedPortfolioId applicablePortfolioId)
    {
        this.userFollowedListener = userFollowedListener;
        this.userToFollow = userToFollow;
        this.applicablePortfolioId = applicablePortfolioId;
        DaggerUtils.inject(this);
    }

    public void launchFollow()
    {
        userProfileCache.register(currentUserId.toUserBaseKey(), this);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    public void launchUnFollow()
    {
        unFollow();
    }

    public void setUserFollowedListener(OnUserFollowedListener userFollowedListener)
    {
        this.userFollowedListener = userFollowedListener;
    }

    @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
    {
        this.currentUserProfile = value;
        follow();
    }

    @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
    {
        notifyFollowFailed(key, error);
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        heroListCacheLazy.get().invalidate(userProfileDTO.getBaseKey());
        updateUserProfileCache(userProfileDTO);
        notifyFollowSuccess(userToFollow, userProfileDTO);
    }

    /**
     * newly added method
     */
    private void updateUserProfileCache(UserProfileDTO userProfileDTO)
    {
        if (userProfileCache != null && currentUserId != null)
        {
            UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
            userProfileCache.put(userBaseKey, userProfileDTO);
        }
    }

    @Override public void failure(RetrofitError error)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        THToast.show(error.getMessage());
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
        requestCode = null;
    }

    protected void follow()
    {
        if (this.currentUserProfile.ccBalance > 0)
        {
            alertDialogUtilLazy.get().showProgressDialog(currentActivityHolderLazy.get()
                    .getCurrentContext(), currentActivityHolderLazy.get().getCurrentContext()
                    .getString(R.string.following_this_hero));
            userServiceWrapper.follow(userToFollow, this);
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
                requestCode = billingInteractor.run(createPurchaseCCRequest());
            }
        }
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
            public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase,
                    UserProfileDTO updatedUserPortfolio)
            {
                notifyFollowSuccess(userToFollow, updatedUserPortfolio);
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

    public static interface OnUserFollowedListener
    {
        void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO);

        void onUserFollowFailed(UserBaseKey userFollowed, Throwable error);
    }
}
