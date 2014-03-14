package com.tradehero.th.billing.googleplay;

import android.content.res.Resources;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.BaseIABLogicHolder;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.th.R;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolderFull
    extends BaseIABLogicHolder<
            IABSKU,
            THIABProductIdentifierFetcherHolder,
            THIABProductDetail,
            THIABInventoryFetcherHolder,
            THIABPurchaseOrder,
            THIABOrderId,
            THIABPurchase,
            THIABPurchaseFetcherHolder,
            THIABPurchaserHolder,
            THIABPurchaseConsumerHolder,
            THIABBillingRequest>
    implements THIABLogicHolder
{
    public static final String TAG = THIABLogicHolderFull.class.getSimpleName();

    protected THIABPurchaseReporterHolder purchaseReporterHolder;

    @Inject protected Lazy<IABSKUListCache> iabskuListCache;
    @Inject protected Lazy<THIABProductDetailCache> thskuDetailCache;

    public THIABLogicHolderFull()
    {
        super();
        purchaseReporterHolder = createPurchaseReporterHolder();
        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
    {
        if (purchaseReporterHolder != null)
        {
            purchaseReporterHolder.onDestroy();
        }
        super.onDestroy();
    }

    @Override public String getBillingHolderName(Resources resources)
    {
        return resources.getString(R.string.th_iab_logic_holder_name);
    }

    @Override public THIABInventoryFetcherHolder getInventoryFetcherHolder()
    {
        return inventoryFetcherHolder;
    }

    @Override public THIABPurchaseFetcherHolder getPurchaseFetcherHolder()
    {
        return purchaseFetcherHolder;
    }

    @Override public THIABPurchaserHolder getPurchaserHolder()
    {
        return purchaserHolder;
    }

    @Override public THIABPurchaseConsumerHolder getPurchaseConsumerHolder()
    {
        return purchaseConsumerHolder;
    }

    @Override public THIABPurchaseReporterHolder getPurchaseReporterHolder()
    {
        return purchaseReporterHolder;
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                purchaseReporterHolder.isUnusedRequestCode(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        purchaseReporterHolder.forgetRequestCode(requestCode);
    }

    @Override public List<THIABProductDetail> getDetailsOfDomain(String domain)
    {
        return ArrayUtils.filter(thskuDetailCache.get().get(getAllSkus()),
                THIABProductDetail.getPredicateIsOfCertainDomain(domain));
    }

    @Override protected BaseIABSKUList<IABSKU> getAllSkus()
    {
        BaseIABSKUList<IABSKU> mixed = iabskuListCache.get().get(IABSKUListType.getInApp());
        BaseIABSKUList<IABSKU> subs = iabskuListCache.get().get(IABSKUListType.getSubs());
        if (subs != null)
        {
            mixed.addAll(subs);
        }
        return mixed;
    }

    @Override protected THIABProductIdentifierFetcherHolder createProductIdentifierFetcherHolder()
    {
        return new THBaseIABProductIdentifierFetcherHolder();
    }

    @Override protected THIABInventoryFetcherHolder createInventoryFetcherHolder()
    {
        return new THBaseIABInventoryFetcherHolder();
    }

    @Override protected THIABPurchaseFetcherHolder createPurchaseFetcherHolder()
    {
        return new THBaseIABPurchaseFetcherHolder();
    }

    @Override protected THIABPurchaserHolder createPurchaserHolder()
    {
        return new THBaseIABPurchaserHolder();
    }

    @Override protected THIABPurchaseConsumerHolder createPurchaseConsumeHolder()
    {
        return new THBaseIABPurchaseConsumerHolder();
    }

    protected THIABPurchaseReporterHolder createPurchaseReporterHolder()
    {
        return new THBaseIABPurchaseReporterHolder();
    }
}
