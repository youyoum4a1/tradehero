package com.tradehero.th.api.purchase;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.exception.UnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:20 PM To change this template use File | Settings | File Templates. */
public class PurchaseReporter extends BasePurchaseReporter<
        IABOrderId,
        IABSKU,
        THSKUDetails,
        SKUPurchase>
{
    public static final String TAG = PurchaseReporter.class.getSimpleName();

    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<AlertPlanService> alertPlanService;
    @Inject Lazy<UserService> userService;
    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;

    private Callback<UserProfileDTO> userProfileDTOCallback;

    public PurchaseReporter()
    {
        DaggerUtils.inject(this);
    }

    @Override public void reportPurchase(SKUPurchase purchase, THSKUDetails skuDetails, int portfolioId)
    {
        reportPurchase(purchase, skuDetails, currentUserBaseKeyHolder.get().getCurrentUserBaseKey(), portfolioId);
    }

    @Override public void reportPurchase(SKUPurchase purchase, THSKUDetails skuDetails, UserBaseKey userBaseKey, int portfolioId)
    {
        this.purchase = purchase;
        this.skuDetails = skuDetails;
        createCallbackIfMissing();

        switch (this.skuDetails.domain)
        {
            case THSKUDetails.DOMAIN_RESET_PORTFOLIO:
                portfolioService.get().resetPortfolio(
                        userBaseKey.key,
                        portfolioId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            case THSKUDetails.DOMAIN_VIRTUAL_DOLLAR:
                portfolioService.get().addCash(
                        userBaseKey.key,
                        portfolioId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            default:
                reportPurchase(purchase, skuDetails, userBaseKey);
                break;
        }
    }

    @Override public void reportPurchase(SKUPurchase purchase, THSKUDetails skuDetails)
    {
        reportPurchase(purchase, skuDetails, currentUserBaseKeyHolder.get().getCurrentUserBaseKey());
    }

    @Override public void reportPurchase(SKUPurchase purchase, THSKUDetails skuDetails, UserBaseKey userBaseKey)
    {
        this.purchase = purchase;
        this.skuDetails = skuDetails;
        createCallbackIfMissing();

        switch (this.skuDetails.domain)
        {
            case THSKUDetails.DOMAIN_STOCK_ALERTS:
                alertPlanService.get().subscribeToAlertPlan(
                        userBaseKey.key,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            case THSKUDetails.DOMAIN_FOLLOW_CREDITS:
                userService.get().addCredit(
                        userBaseKey.key,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;
            default:
                notifyListenerReportFailed(new UnhandledSKUDomainException(this.skuDetails.domain + " is not handled by this method"));
        }
    }

    protected void createCallbackIfMissing()
    {
        if (userProfileDTOCallback == null)
        {
            userProfileDTOCallback = new Callback<UserProfileDTO>()
            {
                @Override public void success(UserProfileDTO userProfileDTO, Response response)
                {
                    handleCallbackSuccess(userProfileDTO, response);
                }

                @Override public void failure(RetrofitError error)
                {
                    handleCallbackFailed(error);
                }
            };
        }
    }

    protected void handleCallbackSuccess(UserProfileDTO userProfileDTO, Response response)
    {
        notifyListenerSuccess(userProfileDTO);
    }

    protected void handleCallbackFailed(RetrofitError error)
    {
        notifyListenerReportFailed(error);
    }
}
