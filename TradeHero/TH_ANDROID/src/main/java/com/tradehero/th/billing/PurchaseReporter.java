package com.tradehero.th.billing;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.exception.UnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.SKUDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:20 PM To change this template use File | Settings | File Templates. */
public class PurchaseReporter extends BasePurchaseReporter<
        THIABOrderId,
        IABSKU,
        THSKUDetails,
        SKUPurchase>
{
    public static final String TAG = PurchaseReporter.class.getSimpleName();

    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;
    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<AlertPlanService> alertPlanService;
    @Inject Lazy<UserService> userService;
    @Inject Lazy<SKUDetailCache> skuDetailCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    private Callback<UserProfileDTO> userProfileDTOCallback;

    public PurchaseReporter()
    {
        DaggerUtils.inject(this);
    }

    private OwnedPortfolioId getApplicableOwnedPortfolioId(SKUPurchase purchase)
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (portfolioId == null)
        {
            portfolioId = portfolioCompactListCache.get().getDefaultPortfolio(currentUserBaseKeyHolder.get().getCurrentUserBaseKey());
        }
        return portfolioId;
    }

    @Override public void reportPurchase(SKUPurchase purchase)
    {
        this.purchase = purchase;
        OwnedPortfolioId portfolioId = getApplicableOwnedPortfolioId(purchase);
        createCallbackIfMissing();

        switch (skuDetailCache.get().get(purchase.getProductIdentifier()).domain)
        {
            case THSKUDetails.DOMAIN_RESET_PORTFOLIO:
                portfolioService.get().resetPortfolio(
                        portfolioId.userId,
                        portfolioId.portfolioId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            case THSKUDetails.DOMAIN_VIRTUAL_DOLLAR:
                portfolioService.get().addCash(
                        portfolioId.userId,
                        portfolioId.portfolioId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            case THSKUDetails.DOMAIN_STOCK_ALERTS:
                alertPlanService.get().subscribeToAlertPlan(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            case THSKUDetails.DOMAIN_FOLLOW_CREDITS:
                userService.get().addCredit(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            default:
                notifyListenerReportFailed(new UnhandledSKUDomainException(skuDetailCache.get().get(purchase.getProductIdentifier()).domain + " is not handled by this method"));
                break;
        }
    }

    @Override public UserProfileDTO reportPurchaseSync(SKUPurchase purchase)
    {
        OwnedPortfolioId portfolioId = getApplicableOwnedPortfolioId(purchase);

        switch (skuDetailCache.get().get(purchase.getProductIdentifier()).domain)
        {
            case THSKUDetails.DOMAIN_RESET_PORTFOLIO:
                return portfolioService.get().resetPortfolio(
                        portfolioId.userId,
                        portfolioId.portfolioId,
                        purchase.getGooglePlayPurchaseDTO());

            case THSKUDetails.DOMAIN_VIRTUAL_DOLLAR:
                return portfolioService.get().addCash(
                        portfolioId.userId,
                        portfolioId.portfolioId,
                        purchase.getGooglePlayPurchaseDTO());

            case THSKUDetails.DOMAIN_STOCK_ALERTS:
                return alertPlanService.get().subscribeToAlertPlan(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO());

            case THSKUDetails.DOMAIN_FOLLOW_CREDITS:
                return userService.get().addCredit(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO());

            default:
                throw new UnhandledSKUDomainException(skuDetailCache.get().get(purchase.getProductIdentifier()).domain + " is not handled by this method");
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
