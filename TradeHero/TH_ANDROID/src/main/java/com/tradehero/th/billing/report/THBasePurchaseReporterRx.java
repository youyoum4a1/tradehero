package com.ayondo.academy.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeActor;
import com.tradehero.common.billing.ProductIdentifier;
import com.ayondo.academy.api.alert.AlertPlanStatusDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.billing.THOrderId;
import com.ayondo.academy.billing.THProductDetail;
import com.ayondo.academy.billing.THProductPurchase;
import com.ayondo.academy.network.service.AlertPlanCheckServiceWrapper;
import com.ayondo.academy.network.service.AlertPlanServiceWrapper;
import com.ayondo.academy.network.service.PortfolioServiceWrapper;
import com.ayondo.academy.network.service.UserServiceWrapper;
import dagger.Lazy;
import rx.Observable;
import rx.functions.Func1;

public class THBasePurchaseReporterRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BaseRequestCodeActor
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
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> get()
    {
        Observable<UserProfileDTO> result;
        switch (productDetail.getDomain())
        {
            case DOMAIN_RESET_PORTFOLIO:
                result = portfolioServiceWrapper.get().resetPortfolioRx(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO());
                break;

            case DOMAIN_VIRTUAL_DOLLAR:
                result = portfolioServiceWrapper.get().addCashRx(
                        purchase.getApplicableOwnedPortfolioId(),
                        purchase.getPurchaseReportDTO());
                break;

            case DOMAIN_STOCK_ALERTS:
                result = alertPlanServiceWrapper.get().subscribeToAlertPlanRx(
                        purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                        purchase.getPurchaseReportDTO())
                        .onErrorResumeNext(new Func1<Throwable, Observable<? extends UserProfileDTO>>()
                        {
                            @Override public Observable<? extends UserProfileDTO> call(Throwable error)
                            {
                                return THBasePurchaseReporterRx.this.seeIfPlanIsYours(error);
                            }
                        });
                break;

            case DOMAIN_FOLLOW_CREDITS:
                //TODO no more free follow
                if (purchase.getUserToFollow() != null)
                {
                    // TODO remove when ok https://www.pivotaltracker.com/story/show/77362688
                    result = userServiceWrapper.get().addCreditRx(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO())
                            .flatMap(new Func1<UserProfileDTO, Observable<? extends UserProfileDTO>>()
                            {
                                @Override public Observable<? extends UserProfileDTO> call(UserProfileDTO userProfileDTO)
                                {
                                    return userServiceWrapper.get().freeFollowRx(purchase.getUserToFollow());
                                }
                            });

                    // TODO put back when ok https://www.pivotaltracker.com/story/show/77362688
                    //return userServiceWrapper.get().followRx(
                    //        purchase.getUserToFollow(),
                    //        purchase.getPurchaseReportDTO())
                    //        .map(this: :createResult);
                }
                else
                {
                    result = userServiceWrapper.get().addCreditRx(
                            purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                            purchase.getPurchaseReportDTO());
                }
                break;

            default:
                result = Observable.error(new IllegalStateException("Unhandled ProductIdentifierDomain." + productDetail.getDomain()));
                break;
        }
        return result.map(new Func1<UserProfileDTO, PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>>()
        {
            @Override public PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> call(UserProfileDTO profile)
            {
                return THBasePurchaseReporterRx.this.createResult(profile);
            }
        });
    }

    @NonNull protected PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType> createResult(
            @NonNull UserProfileDTO userProfileDTO)
    {
        return new PurchaseReportResult<>(getRequestCode(), purchase, userProfileDTO);
    }

    @NonNull protected Observable<UserProfileDTO> seeIfPlanIsYours(@NonNull final Throwable errorFromReport)
    {
        return alertPlanCheckServiceWrapper.get().checkAlertPlanAttributionRx(
                purchase.getApplicableOwnedPortfolioId().getUserBaseKey(),
                purchase.getPurchaseReportDTO())
                .flatMap(new Func1<AlertPlanStatusDTO, Observable<? extends UserProfileDTO>>()
                {
                    @Override public Observable<? extends UserProfileDTO> call(AlertPlanStatusDTO alert)
                    {
                        return THBasePurchaseReporterRx.this.seeIfPlanIsYours(alert, errorFromReport);
                    }
                });
    }

    @NonNull protected Observable<UserProfileDTO> seeIfPlanIsYours(
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
