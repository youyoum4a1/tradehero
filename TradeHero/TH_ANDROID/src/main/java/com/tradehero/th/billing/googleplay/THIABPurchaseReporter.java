package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.googleplay.exception.MissingApplicablePortfolioIdException;
import com.tradehero.th.billing.googleplay.exception.MissingCachedProductDetailException;
import com.tradehero.th.billing.googleplay.exception.PurchaseReportRetrofitException;
import com.tradehero.th.billing.googleplay.exception.PurchaseReportedToOtherUserException;
import com.tradehero.th.billing.googleplay.exception.UnhandledSKUDomainException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class THIABPurchaseReporter extends BasePurchaseReporter<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        IABException>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @Inject Lazy<AlertPlanService> alertPlanService;
    @Inject Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Lazy<UserService> userService;
    @Inject Lazy<THIABProductDetailCache> skuDetailCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    private MiddleCallback<UserProfileDTO> middleCallbackAddCash;

    public THIABPurchaseReporter()
    {
        DaggerUtils.inject(this);
    }

    private OwnedPortfolioId getApplicableOwnedPortfolioId(THIABPurchase purchase) throws MissingApplicablePortfolioIdException
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (portfolioId == null || portfolioId.userId == null || portfolioId.portfolioId == null)
        {
            portfolioId = portfolioCompactListCache.get().getDefaultPortfolio(currentUserId.toUserBaseKey());
        }
        if (portfolioId == null)
        {
            throw new MissingApplicablePortfolioIdException("PortfolioId was not in payload or cache: " + currentUserId.toUserBaseKey());
        }
        return portfolioId;
    }

    @Override public void reportPurchase(int requestCode, THIABPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;
        OwnedPortfolioId portfolioId = null;
        try
        {
            portfolioId = getApplicableOwnedPortfolioId(purchase);
        }
        catch (MissingApplicablePortfolioIdException e)
        {
            Timber.e(e, "Could not get ApplicablePortfolioId");
            notifyListenerReportFailed(e);
            return;
        }

        // TODO do something when info is not available
        THIABProductDetail cachedSkuDetail = skuDetailCache.get().get(purchase.getProductIdentifier());
        if (cachedSkuDetail == null)
        {
            notifyListenerReportFailed(new MissingCachedProductDetailException(purchase.getProductIdentifier() + " is missing from the cache"));
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
                portfolioServiceWrapper.get().resetPortfolio(
                        portfolioId,
                        purchase.getGooglePlayPurchaseDTO(),
                        new THIABPurchaseReporterPurchaseCallback());
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                if (middleCallbackAddCash != null)
                {
                    middleCallbackAddCash.setPrimaryCallback(null);
                }
                middleCallbackAddCash = portfolioServiceWrapper.get().addCash(
                        portfolioId,
                        purchase.getGooglePlayPurchaseDTO(),
                        new THIABPurchaseReporterPurchaseCallback());
                break;

            case DOMAIN_STOCK_ALERTS:
                alertPlanService.get().subscribeToAlertPlan(
                        portfolioId.userId,
                        purchase.getGooglePlayPurchaseDTO(),
                        new THIABPurchaseReporterAlertPlanPurchaseCallback());
                break;

            case DOMAIN_FOLLOW_CREDITS:
                addCredit(portfolioId);
                break;

            default:
                notifyListenerReportFailed(new UnhandledSKUDomainException(skuDetailCache.get().get(purchase.getProductIdentifier()).domain + " is not handled by this method"));
                break;
        }
    }

    private void addCredit(OwnedPortfolioId portfolioId)
    {
        if (purchase.getUserToFollow() != null)
        {
            userServiceWrapper.follow(
                    purchase.getUserToFollow(),
                    purchase.getGooglePlayPurchaseDTO(),
                    new THIABPurchaseReporterPurchaseCallback());
        }
        else
        {
            userServiceWrapper.addCredit(
                    portfolioId.getUserBaseKey(),
                    purchase.getGooglePlayPurchaseDTO(),
                    new THIABPurchaseReporterPurchaseCallback());
        }
    }

    protected void checkAlertPlanAttribution(RetrofitError retrofitErrorFromReport)
    {
        OwnedPortfolioId portfolioId = null;
        try
        {
            portfolioId = getApplicableOwnedPortfolioId(purchase);
        }
        catch (MissingApplicablePortfolioIdException e)
        {
            Timber.e(e, "Could not get ApplicablePortfolioId");
            notifyListenerReportFailed(e);
            return;
        }
        alertPlanServiceWrapper.get().checkAlertPlanAttribution(
                portfolioId.getUserBaseKey(),
                purchase.getGooglePlayPurchaseDTO(),
                new THIABPurchaseReporterAlertPlanStatusCallback(retrofitErrorFromReport));
    }

    protected UserProfileDTO reportAlertPurchaseSync(THIABPurchase purchase) throws IABException
    {
        OwnedPortfolioId portfolioId = getApplicableOwnedPortfolioId(purchase);

        Exception thrown = null;
        try
        {
            return alertPlanService.get().subscribeToAlertPlan(
                    portfolioId.userId,
                    purchase.getGooglePlayPurchaseDTO());
        }
        catch (Exception e)
        {
            thrown = e;
            // Maybe it was already submitted
        }

        try
        {
            AlertPlanStatusDTO statusDTO = alertPlanServiceWrapper.get().checkAlertPlanAttribution(portfolioId.getUserBaseKey(), purchase.getGooglePlayPurchaseDTO());
            if (!statusDTO.isYours)
            {
                throw new PurchaseReportedToOtherUserException("Your alert plan purchase " + purchase + " has already been attributed to another user");
            }

            // Since the purchase was already reported, just return the latest profile
            return alertPlanService.get().checkAlertPlanSubscription(portfolioId.userId);
        }
        catch (Exception e)
        {
            // Since the purchase cannot be found, the previous error was the correct one
            throw new IABException(thrown);
        }
    }

    protected void handleCallbackSuccess(UserProfileDTO userProfileDTO, Response response)
    {
        notifyListenerSuccess(userProfileDTO);
    }

    protected void handleCallbackFailed(RetrofitError error)
    {
        Timber.e(error, "Failed reporting to TradeHero server");
        notifyListenerReportFailed(new PurchaseReportRetrofitException(error));
    }

    protected void handleCallbackStatusSuccess(AlertPlanStatusDTO alertPlanStatusDTO, Response response, RetrofitError errorFromReport)
    {
        OwnedPortfolioId portfolioId = null;
        try
        {
            portfolioId = getApplicableOwnedPortfolioId(purchase);
        }
        catch (MissingApplicablePortfolioIdException e)
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
            alertPlanService.get().checkAlertPlanSubscription(portfolioId.userId, new THIABPurchaseReporterPurchaseCallback());
        }
    }

    protected class THIABPurchaseReporterPurchaseCallback implements Callback<UserProfileDTO>
    {
        public THIABPurchaseReporterPurchaseCallback()
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

    protected class THIABPurchaseReporterAlertPlanPurchaseCallback extends THIABPurchaseReporterPurchaseCallback
    {
        public THIABPurchaseReporterAlertPlanPurchaseCallback()
        {
            super();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            checkAlertPlanAttribution(retrofitError);
        }
    }

    protected class THIABPurchaseReporterAlertPlanStatusCallback implements Callback<AlertPlanStatusDTO>
    {
        protected final RetrofitError errorFromReport;

        public THIABPurchaseReporterAlertPlanStatusCallback(RetrofitError errorFromReport)
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
