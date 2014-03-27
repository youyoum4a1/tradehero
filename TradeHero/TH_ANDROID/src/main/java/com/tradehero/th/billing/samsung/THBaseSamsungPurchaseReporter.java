package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBasePurchaseReporter;
import com.tradehero.th.billing.samsung.exception.SamsungMissingApplicablePortfolioIdException;
import com.tradehero.th.billing.samsung.exception.SamsungMissingCachedProductDetailException;
import com.tradehero.th.billing.samsung.exception.SamsungPurchaseReportRetrofitException;
import com.tradehero.th.billing.samsung.exception.SamsungUnhandledSKUDomainException;
import com.tradehero.th.billing.exception.PurchaseReportedToOtherUserException;
import com.tradehero.th.models.user.MiddleCallbackAddCash;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 12:20 PM To change this template use File | Settings | File Templates. */
public class THBaseSamsungPurchaseReporter
        extends THBasePurchaseReporter<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
    implements THSamsungPurchaseReporter
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @Inject Lazy<AlertPlanService> alertPlanService;
    @Inject Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Lazy<UserService> userService;
    @Inject Lazy<THSamsungProductDetailCache> skuDetailCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    private MiddleCallbackAddCash middleCallbackAddCash;

    public THBaseSamsungPurchaseReporter()
    {
        DaggerUtils.inject(this);
    }

    private OwnedPortfolioId getApplicableOwnedPortfolioId(THSamsungPurchase purchase) throws
            SamsungMissingApplicablePortfolioIdException
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (portfolioId == null || portfolioId.userId == null || portfolioId.portfolioId == null)
        {
            portfolioId = portfolioCompactListCache.get().getDefaultPortfolio(currentUserId.toUserBaseKey());
        }
        if (portfolioId == null)
        {
            throw new SamsungMissingApplicablePortfolioIdException("PortfolioId was not in payload or cache: " + currentUserId.toUserBaseKey());
        }
        return portfolioId;
    }

    @Override public void reportPurchase(int requestCode, THSamsungPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;
        OwnedPortfolioId portfolioId = null;
        try
        {
            portfolioId = getApplicableOwnedPortfolioId(purchase);
        }
        catch (SamsungMissingApplicablePortfolioIdException e)
        {
            Timber.e(e, "Could not get ApplicablePortfolioId");
            notifyListenerReportFailed(e);
            return;
        }

        // TODO do something when info is not available
        THSamsungProductDetail cachedSkuDetail = skuDetailCache.get().get(purchase.getProductIdentifier());
        if (cachedSkuDetail == null)
        {
            notifyListenerReportFailed(new SamsungMissingCachedProductDetailException(purchase.getProductIdentifier() + " is missing from the cache"));
            return;
        }
        if (purchase == null)
        {
            Timber.e("Purchase is null: domain=%s", cachedSkuDetail.domain);
            return;
        }
        if (portfolioId == null)
        {
            Timber.e("portfolioId is null: domain=%s", cachedSkuDetail.domain);
            return;
        }

        switch (cachedSkuDetail.domain)
        {
            case DOMAIN_RESET_PORTFOLIO:
                //portfolioServiceWrapper.get().resetPortfolio(
                //        portfolioId,
                //        purchase.getGooglePlayPurchaseDTO(),
                //        new THSamsungPurchaseReporterPurchaseCallback());
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                if (middleCallbackAddCash != null)
                {
                    middleCallbackAddCash.setPrimaryCallback(null);
                }
                //middleCallbackAddCash = portfolioServiceWrapper.get().addCash(
                //        portfolioId,
                //        purchase.getGooglePlayPurchaseDTO(),
                //        new THSamsungPurchaseReporterPurchaseCallback());
                break;

            case DOMAIN_STOCK_ALERTS:
                //alertPlanService.get().subscribeToAlertPlan(
                //        portfolioId.userId,
                //        purchase.getGooglePlayPurchaseDTO(),
                //        new THSamsungPurchaseReporterAlertPlanPurchaseCallback());
                break;

            case DOMAIN_FOLLOW_CREDITS:
                addCredit(portfolioId);
                break;

            default:
                notifyListenerReportFailed(new SamsungUnhandledSKUDomainException(skuDetailCache.get().get(purchase.getProductIdentifier()).domain + " is not handled by this method"));
                break;
        }
    }

    private void addCredit(OwnedPortfolioId portfolioId)
    {
        if (purchase.getUserToFollow() != null)
        {
            //userServiceWrapper.follow(
            //        purchase.getUserToFollow(),
            //        purchase.getGooglePlayPurchaseDTO(),
            //        new THSamsungPurchaseReporterPurchaseCallback());
        }
        else
        {
            //userService.get().addCredit(
            //        portfolioId.userId,
            //        purchase.getGooglePlayPurchaseDTO(),
            //        new THSamsungPurchaseReporterPurchaseCallback());
        }
    }

    protected void checkAlertPlanAttribution(RetrofitError retrofitErrorFromReport)
    {
        OwnedPortfolioId portfolioId = null;
        try
        {
            portfolioId = getApplicableOwnedPortfolioId(purchase);
        }
        catch (SamsungMissingApplicablePortfolioIdException e)
        {
            Timber.e(e, "Could not get ApplicablePortfolioId");
            notifyListenerReportFailed(e);
            return;
        }
        //alertPlanServiceWrapper.get().checkAlertPlanAttribution(
        //        portfolioId.getUserBaseKey(),
        //        purchase.getGooglePlayPurchaseDTO(),
        //        new THSamsungPurchaseReporterAlertPlanStatusCallback(retrofitErrorFromReport));
    }

    protected void handleCallbackSuccess(UserProfileDTO userProfileDTO, Response response)
    {
        notifyListenerSuccess(userProfileDTO);
    }

    protected void handleCallbackFailed(RetrofitError error)
    {
        Timber.e("Failed reporting to TradeHero server", error);
        notifyListenerReportFailed(new SamsungPurchaseReportRetrofitException(error));
    }

    protected void handleCallbackStatusSuccess(AlertPlanStatusDTO alertPlanStatusDTO, Response response, RetrofitError errorFromReport)
    {
        OwnedPortfolioId portfolioId = null;
        try
        {
            portfolioId = getApplicableOwnedPortfolioId(purchase);
        }
        catch (SamsungMissingApplicablePortfolioIdException e)
        {
            Timber.e(e, "Could not get ApplicablePortfolioId");
            notifyListenerReportFailed(e);
            return;
        }

        if (!alertPlanStatusDTO.isYours)
        {
            // TODO we need to pass a PurchaseReportedToOtherUserException here
            handleCallbackFailed(errorFromReport); // This is not what is intended
        }
        else
        {
            alertPlanService.get().checkAlertPlanSubscription(portfolioId.userId, new THSamsungPurchaseReporterPurchaseCallback());
        }
    }

    protected class THSamsungPurchaseReporterPurchaseCallback implements Callback<UserProfileDTO>
    {
        public THSamsungPurchaseReporterPurchaseCallback()
        {
            super();
        }

        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            handleCallbackSuccess(userProfileDTO, response);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            handleCallbackFailed(retrofitError);
        }
    }

    protected class THSamsungPurchaseReporterAlertPlanPurchaseCallback extends THSamsungPurchaseReporterPurchaseCallback
    {
        public THSamsungPurchaseReporterAlertPlanPurchaseCallback()
        {
            super();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            checkAlertPlanAttribution(retrofitError);
        }
    }

    protected class THSamsungPurchaseReporterAlertPlanStatusCallback implements Callback<AlertPlanStatusDTO>
    {
        protected final RetrofitError errorFromReport;

        public THSamsungPurchaseReporterAlertPlanStatusCallback(RetrofitError errorFromReport)
        {
            super();
            this.errorFromReport = errorFromReport;
        }

        @Override public void success(AlertPlanStatusDTO alertPlanStatusDTO, Response response)
        {
            handleCallbackStatusSuccess(alertPlanStatusDTO, response, errorFromReport);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            // We report on the previous error as this means it was valid
            handleCallbackFailed(errorFromReport);
        }
    }
}
