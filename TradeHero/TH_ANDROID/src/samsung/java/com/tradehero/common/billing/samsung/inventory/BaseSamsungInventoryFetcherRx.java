package com.tradehero.common.billing.samsung.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungItemListOperator;
import com.tradehero.common.billing.samsung.SamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rx.Observable;

/**
 * Product Identifier Fetcher and Inventory Fetcher are essentially making the same calls.
 */
abstract public class BaseSamsungInventoryFetcherRx<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>>
        extends BaseSamsungActorRx<ProductInventoryResult<
        SamsungSKUType,
        SamsungProductDetailType>>
        implements SamsungInventoryFetcherRx<
        SamsungSKUType,
        SamsungProductDetailType>
{
    protected final int startNum;
    protected final int endNum;
    @NonNull protected final String itemType;
    @NonNull protected final List<SamsungSKUType> skus;

    //<editor-fold desc="Constructors">
    public BaseSamsungInventoryFetcherRx(
            int requestCode,
            @NonNull Context context,
            int mode,
            int startNum,
            int endNum,
            @NonNull String itemType,
            @NonNull List<SamsungSKUType> skus)
    {
        super(requestCode, context, mode);
        this.startNum = startNum;
        this.endNum = endNum;
        this.itemType = itemType;
        this.skus = skus;
        fetchInventory();
    }
    //</editor-fold>

    @NonNull @Override public List<SamsungSKUType> getProductIdentifiers()
    {
        return skus;
    }

    @NonNull @Override public Observable<ProductInventoryResult<
            SamsungSKUType,
            SamsungProductDetailType>> get()
    {
        return replayObservable;
    }

    protected void fetchInventory()
    {
        Observable.from(getGroups())
                .flatMap(this::fetchInventoryOnOneGroup)
                .map(detail -> new ProductInventoryResult<>(getRequestCode(), detail.getProductIdentifier(), detail))
                .subscribe(subject);
    }

    @NonNull protected Set<String> getGroups()
    {
        Set<String> groups = new HashSet<>();
        for (SamsungSKUType sku : skus)
        {
            groups.add(sku.groupId);
        }
        return groups;
    }

    @NonNull protected Observable<SamsungProductDetailType> fetchInventoryOnOneGroup(@NonNull final String groupId)
    {
        return Observable.create(new SamsungItemListOperator(context, mode, startNum, endNum, itemType, groupId))
                .flatMap(Observable::from)
                .map(itemVo -> createSamsungProductDetail(new SamsungItemGroup(groupId), itemVo));
    }

    @NonNull abstract protected SamsungProductDetailType createSamsungProductDetail(@NonNull SamsungItemGroup samsungItemGroup, @NonNull ItemVo itemVo);
}
