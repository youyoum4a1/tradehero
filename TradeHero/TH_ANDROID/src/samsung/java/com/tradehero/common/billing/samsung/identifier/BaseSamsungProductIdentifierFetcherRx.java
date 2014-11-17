package com.tradehero.common.billing.samsung.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungItemListOperator;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;

abstract public class BaseSamsungProductIdentifierFetcherRx<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
        extends BaseSamsungActorRx<ProductIdentifierListResult<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType>>
        implements SamsungProductIdentifierFetcherRx<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType>
{
    protected final int startNum;
    protected final int endNum;
    @NonNull protected final String itemType;
    @NonNull protected final String groupId;

    //<editor-fold desc="Constructors">
    public BaseSamsungProductIdentifierFetcherRx(
            int requestCode,
            @NonNull Context context,
            int mode,
            int startNum,
            int endNum,
            @NonNull String itemType,
            @NonNull String groupId)
    {
        super(requestCode, context, mode);
        this.startNum = startNum;
        this.endNum = endNum;
        this.itemType = itemType;
        this.groupId = groupId;
        fetchProductIdentifiers();
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductIdentifierListResult<
            SamsungSKUListKeyType,
            SamsungSKUType,
            SamsungSKUListType>> get()
    {
        return replayObservable;
    }

    protected void fetchProductIdentifiers()
    {
        Observable.create(new SamsungItemListOperator(context, mode, startNum, endNum, itemType, groupId))
                .flatMap(this::createResult)
                .subscribe(subject);
    }

    @NonNull protected Observable<ProductIdentifierListResult<
            SamsungSKUListKeyType,
            SamsungSKUType,
            SamsungSKUListType>> createResult(@NonNull List<ItemVo> itemList)
    {
        Map<SamsungSKUListKeyType, SamsungSKUListType> samsungSKUs = new HashMap<>();
        for (ItemVo itemVo : itemList)
        {
            SamsungSKUListKeyType key = createSamsungListKey(itemVo.getType());
            if (samsungSKUs.get(key) == null)
            {
                samsungSKUs.put(key, createSamsungSKUList());
            }
            samsungSKUs.get(key).add(createSamsungSku(groupId, itemVo.getItemId()));
        }
        return Observable.from(samsungSKUs.entrySet())
                .map(entry -> new ProductIdentifierListResult<>(
                        getRequestCode(),
                        entry.getKey(),
                        entry.getValue()));
    }

    @NonNull abstract protected SamsungSKUListKeyType createSamsungListKey(String itemType);

    @NonNull abstract protected SamsungSKUType createSamsungSku(String groupId, String itemId);

    @NonNull abstract protected SamsungSKUListType createSamsungSKUList();
}
