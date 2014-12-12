package com.tradehero.th.billing.amazon;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonConstants;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.billing.THBillingRequisitePreparer;
import com.tradehero.th.fragments.billing.THAmazonSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THAmazonStoreProductDetailView;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;

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
                THAmazonLogicHolderRx,
                THAmazonStoreProductDetailView,
                THAmazonSKUDetailAdapter>
        implements THAmazonInteractorRx
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NonNull protected final PortfolioCompactListCacheRx portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonInteractorRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THAmazonAlertDialogRxUtil thAmazonAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer,
            @NonNull THAmazonLogicHolderRx billingActor,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTOUtil userProfileDTOUtil,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        super(
                billingActor,
                activityProvider,
                progressDialogUtil,
                thAmazonAlertDialogUtil,
                billingRequisitePreparer);
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
                .map(pair -> pair.second.getDefaultPortfolio())
                .flatMap(dto -> {
                    if (dto == null)
                    {
                        return Observable.error(new IllegalArgumentException("Default portfolio cannot be null"));
                    }
                    return Observable.just(dto);
                });
    }

    @NonNull @Override public Observable<THAmazonPurchaseOrder> createPurchaseOrder(
            @NonNull ProductInventoryResult<AmazonSKU, THAmazonProductDetail> inventoryResult)
    {
        return getDefaultPortfolio()
                .map(dto -> new THAmazonPurchaseOrder(
                        inventoryResult.id,
                        1,
                        dto.getOwnedPortfolioId()));
    }

    @NonNull @Override public Observable<THAmazonPurchaseOrder> createPurchaseOrder(
            @NonNull ProductInventoryResult<AmazonSKU, THAmazonProductDetail> inventoryResult,
            @NonNull UserBaseKey heroId)
    {
        return getDefaultPortfolio()
                .map(dto -> new THAmazonPurchaseOrder(
                        inventoryResult.id,
                        1,
                        dto.getOwnedPortfolioId(),
                        heroId));
    }

    @Override public void manageSubscriptions()
    {
        THToast.show("TODO");
    }
}
