package com.tradehero.th.billing.amazon;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.amazon.AmazonConstants;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class THBaseAmazonInteractorRx
        extends
        THBaseBillingInteractorRx<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonLogicHolderRx>
        implements THAmazonInteractorRx
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NonNull protected final PortfolioCompactListCacheRx portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonInteractorRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull THAmazonAlertDialogRxUtil thAmazonAlertDialogUtil,
            @NonNull THAmazonLogicHolderRx billingActor,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTOUtil userProfileDTOUtil,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        super(
                billingActor,
                activityProvider,
                thAmazonAlertDialogUtil);
        this.currentUserId = currentUserId;
        this.userProfileDTOUtil = userProfileDTOUtil;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @Override @NonNull public String getName()
    {
        return AmazonConstants.NAME;
    }

    @NonNull protected Observable<PortfolioCompactDTO> getDefaultPortfolio()
    {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .take(1)
                .flatMap(new Func1<Pair<UserBaseKey, PortfolioCompactDTOList>, Observable<? extends PortfolioCompactDTO>>()
                {
                    @Override public Observable<? extends PortfolioCompactDTO> call(Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                    {
                        PortfolioCompactDTO dto = pair.second.getDefaultPortfolio();
                        if (dto == null)
                        {
                            return Observable.error(new IllegalArgumentException("Default portfolio cannot be null"));
                        }
                        return Observable.just(dto);
                    }
                });
    }

    @NonNull @Override public Observable<THAmazonPurchaseOrder> createPurchaseOrder(
            @NonNull final THAmazonProductDetail detail)
    {
        return getDefaultPortfolio()
                .map(new Func1<PortfolioCompactDTO, THAmazonPurchaseOrder>()
                {
                    @Override public THAmazonPurchaseOrder call(PortfolioCompactDTO dto)
                    {
                        return new THAmazonPurchaseOrder(
                                detail.getProductIdentifier(),
                                1,
                                dto.getOwnedPortfolioId());
                    }
                });
    }

    @NonNull @Override public Observable<THAmazonPurchaseOrder> createPurchaseOrder(
            @NonNull final THAmazonProductDetail detail,
            @NonNull final UserBaseKey heroId)
    {
        return getDefaultPortfolio()
                .map(new Func1<PortfolioCompactDTO, THAmazonPurchaseOrder>()
                {
                    @Override public THAmazonPurchaseOrder call(PortfolioCompactDTO dto)
                    {
                        return new THAmazonPurchaseOrder(
                                detail.getProductIdentifier(),
                                1,
                                dto.getOwnedPortfolioId(),
                                heroId);
                    }
                });
    }

    @Override public void manageSubscriptions()
    {
        THToast.show("TODO");
    }

    @NonNull @Override public Observable<List<THAmazonProductDetail>> listProduct()
    {
        return super.listProduct().doOnNext(new Action1<List<THAmazonProductDetail>>()
        {
            @Override public void call(List<THAmazonProductDetail> thiabProductDetails)
            {
                for (THAmazonProductDetail productDetail : thiabProductDetails)
                {
                    THAmazonProductDetailTuner.fineTune(productDetail);
                }
            }
        });
    }
}
