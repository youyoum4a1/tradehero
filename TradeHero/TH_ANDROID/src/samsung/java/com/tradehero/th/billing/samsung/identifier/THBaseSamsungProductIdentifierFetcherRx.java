package com.androidth.general.billing.samsung.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;
import com.androidth.general.common.billing.samsung.identifier.BaseSamsungProductIdentifierFetcherRx;
import com.androidth.general.common.billing.samsung.rx.ItemListQueryGroup;
import java.util.Collections;
import java.util.List;

public class THBaseSamsungProductIdentifierFetcherRx
        extends BaseSamsungProductIdentifierFetcherRx<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList>
        implements THSamsungProductIdentifierFetcherRx
{
    public static final int FIRST_ITEM_NUM = 1;

    //<editor-fold desc="Constructors">
    public THBaseSamsungProductIdentifierFetcherRx(
            int requestCode,
            @NonNull Context context,
            @SamsungBillingMode int mode)
    {
        super(requestCode,
                context,
                mode);
    }
    //</editor-fold>

    @NonNull @Override protected List<ItemListQueryGroup> getItemListQueryGroups()
    {
        return Collections.singletonList(
                new ItemListQueryGroup(
                        FIRST_ITEM_NUM,
                        Integer.MAX_VALUE,
                        SamsungIapHelper.ITEM_TYPE_ALL));
    }

    @Override @NonNull protected SamsungSKUListKey createSamsungListKey(String itemType)
    {
        return new SamsungSKUListKey(itemType);
    }

    @Override @NonNull protected SamsungSKU createSamsungSku(String itemId)
    {
        return new SamsungSKU(itemId);
    }

    @Override @NonNull protected SamsungSKUList createSamsungSKUList()
    {
        return new SamsungSKUList();
    }
}
