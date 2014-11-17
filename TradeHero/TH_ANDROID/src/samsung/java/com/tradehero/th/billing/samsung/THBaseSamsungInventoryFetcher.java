package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.samsung.BaseSamsungInventoryFetcher;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import java.util.ArrayList;
import java.util.List;

public class THBaseSamsungInventoryFetcher
        extends BaseSamsungInventoryFetcher<
        SamsungSKU,
        THSamsungProductDetail,
        SamsungException>
        implements THSamsungInventoryFetcher
{
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    public THBaseSamsungInventoryFetcher(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super(requestCode, context, mode);
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @Override protected List<String> getKnownItemGroups()
    {
        List<String> knownGroupIds = new ArrayList<>();
        knownGroupIds.add(THSamsungConstants.IAP_ITEM_GROUP_ID);
        return knownGroupIds;
    }

    @Override protected SamsungSKU createSamsungSku(String groupId, String itemId)
    {
        return new SamsungSKU(groupId, itemId);
    }

    @Override protected THSamsungProductDetail createSamsungProductDetail(SamsungItemGroup samsungItemGroup, ItemVo itemVo)
    {
        return new THSamsungProductDetail(samsungItemGroup, itemVo);
    }

    @Override protected SamsungException createException(ErrorVo errorVo)
    {
        return samsungExceptionFactory.create(errorVo);
    }
}
