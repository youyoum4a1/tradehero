package com.androidth.general.billing.samsung.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.samsung.android.sdk.iap.lib.vo.ItemVo;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.inventory.BaseSamsungInventoryFetcherRx;
import com.androidth.general.common.billing.samsung.rx.ItemListQueryGroup;
import com.androidth.general.billing.samsung.THSamsungProductDetail;
import java.util.List;

public class THBaseSamsungInventoryFetcherRx
        extends BaseSamsungInventoryFetcherRx<
                SamsungSKU,
                THSamsungProductDetail>
        implements THSamsungInventoryFetcherRx
{
    public static final int FIRST_ITEM_NUM = 1;

    //<editor-fold desc="Constructors">
    public THBaseSamsungInventoryFetcherRx(
            int requestCode,
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull List<SamsungSKU> skus)
    {
        super(requestCode, context, mode, skus);
    }
    //</editor-fold>

    @NonNull @Override protected ItemListQueryGroup createItemListQueryGroup(
            @NonNull SamsungSKU sku)
    {
        return new ItemListQueryGroup(
                FIRST_ITEM_NUM,
                Integer.MAX_VALUE,
                SamsungIapHelper.ITEM_TYPE_ALL);
    }

    @Override @NonNull protected THSamsungProductDetail createSamsungProductDetail(
            @NonNull ItemVo itemVo)
    {
        return new THSamsungProductDetail(itemVo);
    }
}
