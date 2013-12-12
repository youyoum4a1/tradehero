package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.exception.UnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.PortfolioServiceUtil;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:20 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseReporter extends BasePurchaseReporter<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception>,
        Exception>
{
    public static final String TAG = THIABPurchaseReporter.class.getSimpleName();

    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;
    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<AlertPlanService> alertPlanService;
    @Inject Lazy<UserService> userService;
    @Inject Lazy<THIABProductDetailCache> skuDetailCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    private Callback<UserProfileDTO> userProfileDTOCallback;

    public THIABPurchaseReporter()
    {
        DaggerUtils.inject(this);
    }

    private OwnedPortfolioId getApplicableOwnedPortfolioId(THIABPurchase purchase)
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (portfolioId == null || portfolioId.userId == null || portfolioId.portfolioId == null)
        {
            portfolioId = portfolioCompactListCache.get().getDefaultPortfolio(currentUserBaseKeyHolder.get().getCurrentUserBaseKey());
        }
        return portfolioId;
    }

    @Override public void reportPurchase(int requestCode, THIABPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;
        OwnedPortfolioId portfolioId = getApplicableOwnedPortfolioId(purchase);
        createCallbackIfMissing();

        // TODO do something when info is not available
        switch (skuDetailCache.get().get(purchase.getProductIdentifier()).domain)
        {
            case THIABProductDetail.DOMAIN_RESET_PORTFOLIO:
                PortfolioServiceUtil.resetPortfolio(portfolioService.get(), portfolioId, purchase.getGooglePlayPurchaseDTO(), userProfileDTOCallback);
                break;

            case THIABProductDetail.DOMAIN_VIRTUAL_DOLLAR:
                PortfolioServiceUtil.addCash(portfolioService.get(), portfolioId, purchase.getGooglePlayPurchaseDTO(), userProfileDTOCallback);
                break;

            case THIABProductDetail.DOMAIN_STOCK_ALERTS:
                alertPlanService.get().subscribeToAlertPlan(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO(),
                        userProfileDTOCallback);
                break;

            case THIABProductDetail.DOMAIN_FOLLOW_CREDITS:
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

    @Override public UserProfileDTO reportPurchaseSync(THIABPurchase purchase) throws RetrofitError
    {
        OwnedPortfolioId portfolioId = getApplicableOwnedPortfolioId(purchase);

        switch (skuDetailCache.get().get(purchase.getProductIdentifier()).domain)
        {
            case THIABProductDetail.DOMAIN_RESET_PORTFOLIO:
                return PortfolioServiceUtil.resetPortfolio(portfolioService.get(), portfolioId, purchase.getGooglePlayPurchaseDTO());

            case THIABProductDetail.DOMAIN_VIRTUAL_DOLLAR:
                return PortfolioServiceUtil.addCash(portfolioService.get(), portfolioId, purchase.getGooglePlayPurchaseDTO());

            case THIABProductDetail.DOMAIN_STOCK_ALERTS:
                return alertPlanService.get().subscribeToAlertPlan(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO());

            case THIABProductDetail.DOMAIN_FOLLOW_CREDITS:
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
        THLog.e(TAG, "Failed reporting to TradeHero server", error);
        notifyListenerReportFailed(error);
    }
}
