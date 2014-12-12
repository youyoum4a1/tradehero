package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.billing.THBillingRequisitePreparer;
import com.tradehero.th.fragments.billing.THIABSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THIABStoreProductDetailView;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class THBaseIABInteractorRx
        extends
        THBaseBillingInteractorRx<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                THIABLogicHolderRx,
                THIABStoreProductDetailView,
                THIABSKUDetailAdapter>
        implements THIABInteractorRx
{
    public static final String BUNDLE_KEY_ACTION = THBaseIABInteractorRx.class.getName() + ".action";

    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final THIABProductDetailCacheRx thiabProductDetailCache;
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NonNull protected final PortfolioCompactListCacheRx portfolioCompactListCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInteractorRx(
            @NonNull THIABLogicHolderRx billingActor,
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THIABAlertDialogRxUtil thIABAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer,
            @NonNull CurrentUserId currentUserId,
            @NonNull THIABProductDetailCacheRx thiabProductDetailCache,
            @NonNull UserProfileDTOUtil userProfileDTOUtil,
            @NonNull PortfolioCompactListCacheRx portfolioCompactListCache)
    {
        super(
                billingActor,
                activityProvider,
                progressDialogUtil,
                thIABAlertDialogUtil,
                billingRequisitePreparer);
        this.currentUserId = currentUserId;
        this.thiabProductDetailCache = thiabProductDetailCache;
        this.userProfileDTOUtil = userProfileDTOUtil;
        this.portfolioCompactListCache = portfolioCompactListCache;
    }
    //</editor-fold>

    @Override @NonNull public String getName()
    {
        return IABConstants.NAME;
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

    @NonNull @Override public Observable<THIABPurchaseOrder> createPurchaseOrder(
            @NonNull ProductInventoryResult<IABSKU, THIABProductDetail> inventoryResult)
    {
        return getDefaultPortfolio()
                .map(dto -> new THIABPurchaseOrder(
                        inventoryResult.id,
                        inventoryResult.detail.getType(),
                        dto.getOwnedPortfolioId()));
    }

    @NonNull @Override public Observable<THIABPurchaseOrder> createPurchaseOrder(
            @NonNull ProductInventoryResult<IABSKU, THIABProductDetail> inventoryResult,
            @NonNull UserBaseKey heroId)
    {
        return getDefaultPortfolio()
                .map(dto -> new THIABPurchaseOrder(
                        inventoryResult.id,
                        inventoryResult.detail.getType(),
                        dto.getOwnedPortfolioId(),
                        heroId));
    }

    @Override public void manageSubscriptions()
    {
        Activity currentActivity = activityProvider.get();
        if (currentActivity != null)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(IABConstants.GOOGLE_PLAY_ACCOUNT_URL));
            currentActivity.startActivity(intent);
        }
    }
}
