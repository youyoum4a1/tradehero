package com.tradehero.th.billing.samsung;

import android.content.Intent;
import android.content.res.Resources;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBaseBillingLogicHolder;
import com.tradehero.th.billing.samsung.persistence.THSamsungGroupItemCache;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.samsung.SamsungSKUListCache;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THSamsungLogicHolderFull
    extends THBaseBillingLogicHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductDetail,
        THSamsungProductDetailTuner,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungRequestFull,
        SamsungException>
    implements THSamsungLogicHolder
{
    public static final String TAG = THSamsungLogicHolderFull.class.getSimpleName();

    private SamsungSKUListCache samsungSkuListCache;
    private THSamsungProductDetailCache thskuDetailCache;
    private THSamsungGroupItemCache groupItemCache;

    @Inject public THSamsungLogicHolderFull(UserProfileCache userProfileCache,
            UserServiceWrapper userServiceWrapper,
            HeroListCache heroListCache, SamsungSKUListCache samsungSkuListCache,
            THSamsungProductDetailCache thskuDetailCache,
            THSamsungGroupItemCache groupItemCache)
    {
        super(userProfileCache, userServiceWrapper, heroListCache);
        this.samsungSkuListCache = samsungSkuListCache;
        this.thskuDetailCache = thskuDetailCache;
        this.groupItemCache = groupItemCache;
    }

     //<editor-fold desc="Life Cycle">
    @Override protected THSamsungProductIdentifierFetcherHolder createProductIdentifierFetcherHolder()
    {
        return new THBaseSamsungProductIdentifierFetcherHolder();
    }

    @Override protected THSamsungInventoryFetcherHolder createInventoryFetcherHolder()
    {
        return new THBaseSamsungInventoryFetcherHolder();
    }

    @Override protected THSamsungPurchaseFetcherHolder createPurchaseFetcherHolder()
    {
        return new THBaseSamsungPurchaseFetcherHolder();
    }

    @Override protected THSamsungPurchaserHolder createPurchaserHolder()
    {
        return new THBaseSamsungPurchaserHolder();
    }

    @Override protected THSamsungBillingAvailableTesterHolder createBillingAvailableTesterHolder()
    {
        return new THBaseSamsungBillingAvailableTesterHolder();
    }

    @Override protected THSamsungPurchaseReporterHolder createPurchaseReporterHolder()
    {
        return new THBaseSamsungPurchaseReporterHolder();
    }
    //</editor-fold>

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_samsung_logic_holder_name);
    }

    @Override public List<THSamsungProductDetail> getDetailsOfDomain(ProductIdentifierDomain domain)
    {
        List<THSamsungProductDetail> details = thskuDetailCache.get(getAllSkus());
        if (details == null)
        {
            return null;
        }
        return ArrayUtils.filter(
                details,
                new THProductDetailDomainPredicate<SamsungSKU, THSamsungProductDetail>(domain));
    }

    protected BaseSamsungSKUList<SamsungSKU> getAllSkus()
    {
        return samsungSkuListCache.get(SamsungSKUListKey.getAllKey());
    }

    //<editor-fold desc="Run Logic">
    @Override protected boolean runInternal(int requestCode)
    {
        boolean launched = super.runInternal(requestCode);
        THSamsungRequestFull billingRequest = billingRequests.get(requestCode);
        if (!launched && billingRequest != null)
        {
            if (billingRequest.restorePurchase && billingRequest.fetchedPurchases != null)
            {
                boolean prepared = billingRequest.fetchedPurchases.size() > 0 && prepareToRestoreOnePurchase(requestCode, billingRequest);
                if (prepared)
                {
                    launched = runInternal(requestCode);
                }

                if (!launched)
                {
                    notifyPurchaseRestored(requestCode, billingRequest.restoredPurchases, billingRequest.restoreFailedPurchases, billingRequest.restoreFailedErrors);
                }
            }
        }
        return launched;
    }
    //</editor-fold>

    //<editor-fold desc="Sequence Logic">
    @Override protected void prepareRequestForNextRunAfterPurchaseFetchedSuccess(int requestCode, List<THSamsungPurchase> purchases)
    {
        super.prepareRequestForNextRunAfterPurchaseFetchedSuccess(requestCode, purchases);
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseFinished(int requestCode, THSamsungPurchaseOrder purchaseOrder, THSamsungPurchase purchase)
    {
        super.prepareRequestForNextRunAfterPurchaseFinished(requestCode, purchaseOrder, purchase);
        THSamsungRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            billingRequest.purchaseToReport = purchase;
        }
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseReportedSuccess(int requestCode, THSamsungPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        super.prepareRequestForNextRunAfterPurchaseReportedSuccess(requestCode, reportedPurchase,
                updatedUserPortfolio);
    }

    @Override protected void prepareRequestForNextRunAfterPurchaseReportedFailed(int requestCode, THSamsungPurchase reportedPurchase, SamsungException error)
    {
        super.prepareRequestForNextRunAfterPurchaseReportedFailed(requestCode, reportedPurchase, error);
        THSamsungRequestFull billingRequest = billingRequests.get(requestCode);
        if (billingRequest != null)
        {
            if (billingRequest.restorePurchase)
            {
                Timber.e(error, "Failed to report a purchase to be restored");
                billingRequest.restoreFailedPurchases.add(reportedPurchase);
                billingRequest.restoreFailedErrors.add(error);
                prepareToRestoreOnePurchase(requestCode, billingRequest);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Product Identifier">
    @Override protected ProductIdentifierListCache<SamsungSKU, SamsungSKUListKey, SamsungSKUList> getProductIdentifierCache()
    {
        return samsungSkuListCache;
    }
    //</editor-fold>

    //<editor-fold desc="Fetch Inventory">
    @Override public void launchInventoryFetchSequence(int requestCode, List<SamsungSKU> allIds)
    {
        List<SamsungSKU> groupValues = allIds == null ? groupItemCache.get(THSamsungConstants.getItemGroupId()) : null;
        boolean allIn = true;
        if (groupValues != null)
        {
            for (SamsungSKU id : groupValues)
            {
                allIn &= groupValues.contains(id);
            }
        }
        else
        {
            allIn = false;
        }

        Map<SamsungSKU, THSamsungProductDetail> details = thskuDetailCache.getMap(groupValues);
        if (groupValues != null && details != null)
        {
            for (SamsungSKU id : groupValues)
            {
                allIn &= details.containsKey(id) && details.get(id) != null;
            }
        }
        else
        {
            allIn = false;
        }

        if (allIn)
        {
            handleInventoryFetchedSuccess(requestCode, groupValues, details);
        }
        else
        {
            super.launchInventoryFetchSequence(requestCode, allIds);
        }
    }

    @Override protected void handleInventoryFetchedSuccess(int requestCode, List<SamsungSKU> productIdentifiers, Map<SamsungSKU, THSamsungProductDetail> inventory)
    {
        groupItemCache.add(productIdentifiers);
        if (inventory != null)
        {
            groupItemCache.add(inventory.keySet());
        }
        super.handleInventoryFetchedSuccess(requestCode, productIdentifiers, inventory);
    }

    @Override protected ProductDetailCache<SamsungSKU, THSamsungProductDetail, THSamsungProductDetailTuner> getProductDetailCache()
    {
        return thskuDetailCache;
    }
    //</editor-fold>

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
