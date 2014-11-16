package com.tradehero.th.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeReplayActor;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.api.alert.AlertPlanStatusDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THOrderId;
import com.tradehero.th.billing.THProductDetail;
import com.tradehero.th.billing.THProductPurchase;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import rx.Observable;

abstract public class THBasePurchaseReporterRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BaseRequestCodeReplayActor<PurchaseReportResult<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType>>
        implements THPurchaseReporterRx<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType>
{
    @NonNull protected final THProductPurchaseType purchase;
    @NonNull protected THProductDetailType productDetail;
    @NonNull protected final Lazy<? extends UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<? extends AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<? extends AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<? extends PortfolioServiceWrapper> portfolioServiceWrapper;

    //<editor-fold desc="Constructors">
    protected THBasePurchaseReporterRx(
            int requestCode,
            @NonNull THProductPurchaseType purchase,
            @NonNull THProductDetailType productDetail,
            @NonNull Lazy<? extends AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<? extends AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<? extends UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<? extends PortfolioServiceWrapper> portfolioServiceWrapper)
    {
        super(requestCode);
        this.purchase = purchase;
        this.productDetail = productDetail;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        reportPurchase();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> get()
    {
        return replayObservable;
    }

    protected void reportPurchase()
    {
        switch (productDetail.getDomain())
        {
            case DOMAIN_RESET_PORTFOLIO:
                portfolioServiceWrapper.get().resetPortfolioRx(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO())
                        .map(this::createResult)
                        .subscribe(subject);
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                portfolioServiceWrapper.get().addCashRx(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO())
                        .map(this::createResult)
                        .subscribe(subject);
                break;

            case DOMAIN_STOCK_ALERTS:
                alertPlanServiceWrapper.get().subscribeToAlertPlanRx(
                        purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                        purchase.getPurchaseReportDTO())
                        .onErrorResumeNext(this::seeIfPlanIsYours)
                        .map(this::createResult)
                        .subscribe(subject);
                break;

            case DOMAIN_FOLLOW_CREDITS:
                if (purchase.getUserToFollow() != null)
                {
                    // TODO remove when ok https://www.pivotaltracker.com/story/show/77362688
                    userServiceWrapper.get().addCreditRx(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO())
                            .flatMap(userProfileDTO -> userServiceWrapper.get().followRx(purchase.getUserToFollow()))
                            .map(this::createResult)
                            .subscribe(subject);

                    // TODO put back when ok https://www.pivotaltracker.com/story/show/77362688
                    //userServiceWrapper.get().followRx(
                    //        purchase.getUserToFollow(),
                    //        purchase.getPurchaseReportDTO())
                    //        .map(this::createResult)
                    //        .subscribe(subject);
                }
                else
                {
                    userServiceWrapper.get().addCreditRx(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO())
                            .map(this::createResult)
                            .subscribe(subject);
                }
                break;

            default:
                subject.onError(new IllegalStateException("Unhandled ProductIdentifierDomain." + productDetail.getDomain()));
                break;
        }
    }

    @NonNull abstract protected PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> createResult(
            @NonNull UserProfileDTO userProfileDTO);

    protected Observable<UserProfileDTO> seeIfPlanIsYours(@NonNull Throwable errorFromReport)
    {
        return alertPlanCheckServiceWrapper.get().checkAlertPlanAttributionRx(
                purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                purchase.getPurchaseReportDTO())
                .flatMap(alert -> seeIfPlanIsYours(alert, errorFromReport));
    }

    protected Observable<UserProfileDTO> seeIfPlanIsYours(
            @NonNull AlertPlanStatusDTO alertPlanStatusDTO,
            @NonNull Throwable errorFromReport)
    {
        OwnedPortfolioId portfolioId = purchase.getApplicableOwnedPortfolioId();
        if (!alertPlanStatusDTO.isYours)
        {
            // TODO we need to pass a PurchaseReportedToOtherUserException here
            Observable.error(errorFromReport); // This is not what is intended
        }
        return alertPlanServiceWrapper.get().checkAlertPlanSubscriptionRx(portfolioId.getUserBaseKey());
    }
}
