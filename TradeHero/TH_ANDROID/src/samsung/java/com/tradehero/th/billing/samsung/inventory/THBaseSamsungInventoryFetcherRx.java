package com.tradehero.th.billing.samsung.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.inventory.BaseSamsungInventoryFetcherRx;
import com.tradehero.common.billing.samsung.rx.ItemListQueryGroup;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
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
            int mode,
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
                SamsungIapHelper.ITEM_TYPE_ALL,
                THSamsungConstants.IAP_ITEM_GROUP_ID);
    }

    @Override @NonNull protected THSamsungProductDetail createSamsungProductDetail(
            @NonNull SamsungItemGroup samsungItemGroup,
            @NonNull ItemVo itemVo)
    {
        return new THSamsungProductDetail(samsungItemGroup, itemVo);
    }
}
