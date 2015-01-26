package com.tradehero.common.billing.samsung.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.common.billing.samsung.BaseSamsungActorRx;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.rx.ItemListQueryGroup;
import com.tradehero.common.billing.samsung.rx.SamsungItemListOperatorZip;
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

    //<editor-fold desc="Constructors">
    public BaseSamsungProductIdentifierFetcherRx(
            int requestCode,
            @NonNull Context context,
            int mode)
    {
        super(requestCode, context, mode);
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductIdentifierListResult<
            SamsungSKUListKeyType,
            SamsungSKUType,
            SamsungSKUListType>> get()
    {
        return new SamsungItemListOperatorZip(context, mode, getItemListQueryGroups())
                .getItems()
                .flatMap(this::createResult);
    }

    @NonNull protected Observable<ProductIdentifierListResult<
            SamsungSKUListKeyType,
            SamsungSKUType,
            SamsungSKUListType>> createResult(@NonNull Pair<ItemListQueryGroup, List<ItemVo>> pair)
    {
        Map<SamsungSKUListKeyType, SamsungSKUListType> samsungSKUs = new HashMap<>();
        for (ItemVo itemVo : pair.second)
        {
            SamsungSKUListKeyType key = createSamsungListKey(itemVo.getType());
            if (samsungSKUs.get(key) == null)
            {
                samsungSKUs.put(key, createSamsungSKUList());
            }
            samsungSKUs.get(key).add(createSamsungSku(pair.first.groupId, itemVo.getItemId()));
        }
        return Observable.from(samsungSKUs.entrySet())
                .map(entry -> new ProductIdentifierListResult<>(
                        getRequestCode(),
                        entry.getKey(),
                        entry.getValue()));
    }

    @NonNull abstract protected List<ItemListQueryGroup> getItemListQueryGroups();

    @NonNull abstract protected SamsungSKUListKeyType createSamsungListKey(String itemType);

    @NonNull abstract protected SamsungSKUType createSamsungSku(String groupId, String itemId);

    @NonNull abstract protected SamsungSKUListType createSamsungSKUList();
}
