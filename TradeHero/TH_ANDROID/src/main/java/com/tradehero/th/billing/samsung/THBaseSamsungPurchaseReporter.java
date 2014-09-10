package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBasePurchaseReporter;
import com.tradehero.th.billing.samsung.exception.SamsungMissingApplicablePortfolioIdException;
import com.tradehero.th.billing.samsung.exception.SamsungMissingCachedProductDetailException;
import com.tradehero.th.billing.samsung.exception.SamsungPurchaseReportRetrofitException;
import com.tradehero.th.billing.samsung.exception.SamsungUnhandledSKUDomainException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.AlertPlanServiceAsync;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import dagger.Lazy;
import java.io.IOException;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class THBaseSamsungPurchaseReporter
        extends THBasePurchaseReporter<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
    implements THSamsungPurchaseReporter
{
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NotNull protected final Lazy<AlertPlanService> alertPlanService;
    @NotNull protected final Lazy<AlertPlanServiceAsync> alertPlanServiceAsync;
    @NotNull protected final Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NotNull protected final UserServiceWrapper userServiceWrapper;
    @NotNull protected final Lazy<UserService> userService;
    @NotNull protected final Lazy<THSamsungProductDetailCache> skuDetailCache;
    @NotNull protected final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Nullable private MiddleCallback<UserProfileDTO> middleCallbackAddCash;
    @Nullable private MiddleCallback<UserProfileDTO> middleCallbackAddCredit;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseReporter(
            @NotNull CurrentUserId currentUserId,
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<AlertPlanService> alertPlanService,
            @NotNull Lazy<AlertPlanServiceAsync> alertPlanServiceAsync,
            @NotNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull Lazy<UserService> userService,
            @NotNull Lazy<THSamsungProductDetailCache> skuDetailCache,
            @NotNull Lazy<PortfolioCompactListCache> portfolioCompactListCache)
    {
        this.currentUserId = currentUserId;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.alertPlanService = alertPlanService;
        this.alertPlanServiceAsync = alertPlanServiceAsync;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.userService = userService;
        this.skuDetailCache = skuDetailCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    private OwnedPortfolioId getApplicableOwnedPortfolioId(THSamsungPurchase purchase) throws
            SamsungMissingApplicablePortfolioIdException
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (portfolioId == null || portfolioId.userId == null || portfolioId.portfolioId == null)
        {
            PortfolioCompactDTO cachedDefaultPortfolio = portfolioCompactListCache.get().getDefaultPortfolio(currentUserId.toUserBaseKey());
            if (cachedDefaultPortfolio != null)
            {
                portfolioId = cachedDefaultPortfolio.getOwnedPortfolioId();
            }
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
                portfolioServiceWrapper.get().resetPortfolio(
                        portfolioId,
                        purchase.getPurchaseDTO(),
                        new THSamsungPurchaseReporterPurchaseCallback());
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                if (middleCallbackAddCash != null)
                {
                    middleCallbackAddCash.setPrimaryCallback(null);
                }
                middleCallbackAddCash = portfolioServiceWrapper.get().addCash(
                        portfolioId,
                        purchase.getPurchaseDTO(),
                        new THSamsungPurchaseReporterPurchaseCallback());
                break;

            case DOMAIN_STOCK_ALERTS:
                alertPlanServiceAsync.get().subscribeToAlertPlan(
                        portfolioId.userId,
                        purchase.getPurchaseDTO(),
                        new THSamsungPurchaseReporterAlertPlanPurchaseCallback());
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
            userServiceWrapper.follow(
                    purchase.getUserToFollow(),
                    purchase.getPurchaseDTO(),
                    new THSamsungPurchaseReporterPurchaseCallback());
        }
        else
        {
            if (middleCallbackAddCredit != null)
            {
                middleCallbackAddCredit.setPrimaryCallback(null);
            }
            middleCallbackAddCredit = userServiceWrapper.addCredit(
                    portfolioId.getUserBaseKey(),
                    purchase.getPurchaseDTO(),
                    new THSamsungPurchaseReporterPurchaseCallback());
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
        alertPlanServiceWrapper.get().checkAlertPlanAttribution(
                portfolioId.getUserBaseKey(),
                purchase.getPurchaseDTO(),
                new THSamsungPurchaseReporterAlertPlanStatusCallback(retrofitErrorFromReport));
    }

    protected void handleCallbackSuccess(UserProfileDTO userProfileDTO, Response response)
    {
        notifyListenerSuccess(userProfileDTO);
    }

    protected void handleCallbackFailed(RetrofitError error)
    {
        Timber.e(error, "Failed reporting to TradeHero server");
        Timber.d("Is network error %s", error.isNetworkError());
        Timber.d("url %s", error.getUrl());
        try
        {
            Timber.d("body %s", IOUtils.errorToBodyString(error));
        }
        catch (IOException e)
        {
            Timber.e(e, "Failed to decode error body");
        }
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
            alertPlanServiceAsync.get().checkAlertPlanSubscription(portfolioId.userId, new THSamsungPurchaseReporterPurchaseCallback());
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
