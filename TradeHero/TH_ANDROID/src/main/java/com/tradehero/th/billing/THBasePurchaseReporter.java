package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import dagger.Lazy;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

abstract public class THBasePurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THProductDetailTunerType extends THProductDetailTuner<ProductIdentifierType, THProductDetailType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
        implements THPurchaseReporter<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType,
        BillingExceptionType>
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final Lazy<? extends UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<? extends AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<? extends AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<? extends PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NonNull protected final Lazy<? extends PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<? extends ProductDetailCache<
            ProductIdentifierType,
            THProductDetailType,
            THProductDetailTunerType>> productDetailCache;

    protected int requestCode;
    protected THProductPurchaseType purchase;
    protected THProductDetailType productDetail;
    @Nullable private THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener;

    //<editor-fold desc="Constructors">
    protected THBasePurchaseReporter(
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<? extends AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<? extends AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<? extends UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<? extends PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<? extends PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<? extends ProductDetailCache<
                    ProductIdentifierType,
                    THProductDetailType,
                    THProductDetailTunerType>> productDetailCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.productDetailCache = productDetailCache;
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return requestCode;
    }

    @Override @Nullable public THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> getPurchaseReporterListener()
    {
        return this.listener;
    }

    @Override public void setPurchaseReporterListener(@Nullable THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener)
    {
        this.listener = listener;
    }

    /**
     * @return true if handled, false otherwise
     */
    protected boolean reportPurchase()
    {
        boolean handled = false;
        switch (productDetail.getDomain())
        {
            case DOMAIN_RESET_PORTFOLIO:
                handled = true;
                portfolioServiceWrapper.get().resetPortfolio(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO(),
                        createPurchaseReportedCallback());
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                handled = true;
                portfolioServiceWrapper.get().addCash(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO(),
                        createPurchaseReportedCallback());
                break;

            case DOMAIN_STOCK_ALERTS:
                handled = true;
                alertPlanServiceWrapper.get().subscribeToAlertPlan(
                        purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                        purchase.getPurchaseReportDTO(),
                        createAlertPlanPurchaseCallback());
                break;

            case DOMAIN_FOLLOW_CREDITS:
                handled = true;
                if (purchase.getUserToFollow() != null)
                {
                    // TODO remove when ok https://www.pivotaltracker.com/story/show/77362688
                    userServiceWrapper.get().addCredit(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO(),
                            tempCreatePurchaseReportedCreditBeforeFollowCallback());
                    //userServiceWrapper.get().follow( // TODO put back when ok https://www.pivotaltracker.com/story/show/77362688
                    //        purchase.getUserToFollow(),
                    //        purchase.getPurchaseReportDTO(),
                    //        createPurchaseReportedCallback());
                }
                else
                {
                    userServiceWrapper.get().addCredit(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO(),
                            createPurchaseReportedCallback());
                }
                break;
        }
        //noinspection ConstantConditions
        return handled;
    }

    @NonNull protected Callback<UserProfileDTO> createPurchaseReportedCallback()
    {
        return new THBasePurchaseReporterPurchaseCallback();
    }

    protected class THBasePurchaseReporterPurchaseCallback implements Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            handleCallbackSuccess(userProfileDTO, response);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            handleCallbackFailed(retrofitError);
        }
    }

    protected void handleCallbackSuccess(UserProfileDTO userProfileDTO, @SuppressWarnings("UnusedParameters") Response response)
    {
        notifyListenerSuccess(userProfileDTO);
    }

    abstract protected void handleCallbackFailed(RetrofitError error);

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(requestCode, this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final BillingExceptionType error)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(requestCode, this.purchase, error);
        }
    }

    @Deprecated // Remove when this is ok https://www.pivotaltracker.com/story/show/77362688
    protected Callback<UserProfileDTO> tempCreatePurchaseReportedCreditBeforeFollowCallback()
    {
        return new TempTHBasePurchaseReportedCreditBeforeFollowCallback();
    }

    @Deprecated // Remove when this is ok https://www.pivotaltracker.com/story/show/77362688
    protected class TempTHBasePurchaseReportedCreditBeforeFollowCallback
        extends THBasePurchaseReporterPurchaseCallback
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            userServiceWrapper.get().follow(
                    purchase.getUserToFollow(),
                    createPurchaseReportedCallback());
        }
    }

    protected Callback<UserProfileDTO> createAlertPlanPurchaseCallback()
    {
        return new THBasePurchaseReporterAlertPlanPurchaseCallback();
    }

    protected class THBasePurchaseReporterAlertPlanPurchaseCallback extends THBasePurchaseReporterPurchaseCallback
    {
        @Override public void failure(RetrofitError retrofitError)
        {
            checkAlertPlanAttribution(retrofitError);
        }
    }

    protected void checkAlertPlanAttribution(RetrofitError retrofitErrorFromReport)
    {
        alertPlanCheckServiceWrapper.get().checkAlertPlanAttribution(
                purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                purchase.getPurchaseReportDTO(),
                new THBasePurchaseReporterAlertPlanStatusCallback(retrofitErrorFromReport));
    }

    protected class THBasePurchaseReporterAlertPlanStatusCallback implements Callback<AlertPlanStatusDTO>
    {
        protected final RetrofitError errorFromReport;

        public THBasePurchaseReporterAlertPlanStatusCallback(RetrofitError errorFromReport)
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

    protected void handleCallbackStatusSuccess(
            AlertPlanStatusDTO alertPlanStatusDTO,
            @SuppressWarnings("UnusedParameters") Response response,
            RetrofitError errorFromReport)
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (!alertPlanStatusDTO.isYours)
        {
            // TODO we need to pass a PurchaseReportedToOtherUserException here
            handleCallbackFailed(errorFromReport); // This is not what is intended
        }
        else
        {
            alertPlanServiceWrapper.get().checkAlertPlanSubscription(portfolioId.getUserBaseKey(), createPurchaseReportedCallback());
        }
    }
}
