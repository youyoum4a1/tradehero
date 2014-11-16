package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.BaseRequestCodeActor;
import com.tradehero.common.billing.ProductDetailCacheRx;
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
import retrofit.RetrofitError;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

abstract public class THBasePurchaseReporter<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THProductDetailTunerType extends THProductDetailTuner<ProductIdentifierType, THProductDetailType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        BillingExceptionType extends BillingException>
    extends BaseRequestCodeActor
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
    @NonNull protected final Lazy<? extends ProductDetailCacheRx<
                ProductIdentifierType,
                THProductDetailType,
                THProductDetailTunerType>> productDetailCache;

    protected THProductPurchaseType purchase;
    protected THProductDetailType productDetail;
    @Nullable private THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener;

    //<editor-fold desc="Constructors">
    protected THBasePurchaseReporter(
            int requestCode,
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<? extends AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<? extends AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<? extends UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<? extends PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<? extends PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<? extends ProductDetailCacheRx<
                    ProductIdentifierType,
                    THProductDetailType,
                    THProductDetailTunerType>> productDetailCache)
    {
        super(requestCode);
        this.currentUserId = currentUserId;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.productDetailCache = productDetailCache;
    }
    //</editor-fold>

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
                portfolioServiceWrapper.get().resetPortfolioRx(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createPurchaseReportedObserver());
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                handled = true;
                portfolioServiceWrapper.get().addCashRx(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createPurchaseReportedObserver());
                break;

            case DOMAIN_STOCK_ALERTS:
                handled = true;
                alertPlanServiceWrapper.get().subscribeToAlertPlanRx(
                        purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                        purchase.getPurchaseReportDTO())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createAlertPlanPurchaseObserver());
                break;

            case DOMAIN_FOLLOW_CREDITS:
                handled = true;
                if (purchase.getUserToFollow() != null)
                {
                    // TODO remove when ok https://www.pivotaltracker.com/story/show/77362688
                    userServiceWrapper.get().addCreditRx(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(tempCreatePurchaseReportedCreditBeforeFollowObserver());
                    //userServiceWrapper.get().follow( // TODO put back when ok https://www.pivotaltracker.com/story/show/77362688
                    //        purchase.getUserToFollow(),
                    //        purchase.getPurchaseReportDTO(),
                    //        createPurchaseReportedObserver());
                }
                else
                {
                    userServiceWrapper.get().addCreditRx(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(createPurchaseReportedObserver());
                }
                break;
        }
        //noinspection ConstantConditions
        return handled;
    }

    @NonNull protected Observer<UserProfileDTO> createPurchaseReportedObserver()
    {
        return new THBasePurchaseReporterPurchaseObserver();
    }

    protected class THBasePurchaseReporterPurchaseObserver extends EmptyObserver<UserProfileDTO>
    {
        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            handleCallbackSuccess(userProfileDTO);
        }

        @Override public void onError(Throwable e)
        {
            if (e instanceof RetrofitError)
            {
                handleCallbackFailed((RetrofitError) e);
            }
        }
    }

    protected void handleCallbackSuccess(UserProfileDTO userProfileDTO)
    {
        notifyListenerSuccess(userProfileDTO);
    }

    abstract protected void handleCallbackFailed(RetrofitError error);

    protected void notifyListenerSuccess(final UserProfileDTO updatedUserPortfolio)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReported(getRequestCode(), this.purchase, updatedUserPortfolio);
        }
    }

    protected void notifyListenerReportFailed(final BillingExceptionType error)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> listener1 = getPurchaseReporterListener();
        if (listener1 != null)
        {
            listener1.onPurchaseReportFailed(getRequestCode(), this.purchase, error);
        }
    }

    @Deprecated // Remove when this is ok https://www.pivotaltracker.com/story/show/77362688
    protected Observer<UserProfileDTO> tempCreatePurchaseReportedCreditBeforeFollowObserver()
    {
        return new TempTHBasePurchaseReportedCreditBeforeFollowObserver();
    }

    @Deprecated // Remove when this is ok https://www.pivotaltracker.com/story/show/77362688
    protected class TempTHBasePurchaseReportedCreditBeforeFollowObserver
        extends THBasePurchaseReporterPurchaseObserver
    {
        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            userServiceWrapper.get().followRx(
                    purchase.getUserToFollow())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createPurchaseReportedObserver());
        }
    }

    protected Observer<UserProfileDTO> createAlertPlanPurchaseObserver()
    {
        return new THBasePurchaseReporterAlertPlanPurchaseObserver();
    }

    protected class THBasePurchaseReporterAlertPlanPurchaseObserver extends THBasePurchaseReporterPurchaseObserver
    {
        @Override public void onError(Throwable e)
        {
            checkAlertPlanAttribution((RetrofitError) e);
        }
    }

    protected void checkAlertPlanAttribution(RetrofitError retrofitErrorFromReport)
    {
        alertPlanCheckServiceWrapper.get().checkAlertPlanAttributionRx(
                purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                purchase.getPurchaseReportDTO())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new THBasePurchaseReporterAlertPlanStatusObserver(retrofitErrorFromReport));
    }

    protected class THBasePurchaseReporterAlertPlanStatusObserver extends EmptyObserver<AlertPlanStatusDTO>
    {
        protected final RetrofitError errorFromReport;

        public THBasePurchaseReporterAlertPlanStatusObserver(RetrofitError errorFromReport)
        {
            super();
            this.errorFromReport = errorFromReport;
        }

        @Override public void onNext(AlertPlanStatusDTO args)
        {
            handleCallbackStatusSuccess(args, errorFromReport);
        }

        @Override public void onError(Throwable e)
        {
            // We report on the previous error as this means it was valid
            handleCallbackFailed(errorFromReport);
        }
    }

    protected void handleCallbackStatusSuccess(
            AlertPlanStatusDTO alertPlanStatusDTO,
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
            alertPlanServiceWrapper.get().checkAlertPlanSubscriptionRx(portfolioId.getUserBaseKey())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createPurchaseReportedObserver());
        }
    }
}
