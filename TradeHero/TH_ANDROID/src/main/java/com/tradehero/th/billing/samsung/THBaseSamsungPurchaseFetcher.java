package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcher;
import com.tradehero.common.billing.samsung.SamsungPurchaseCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by xavier on 3/27/14.
 */
public class THBaseSamsungPurchaseFetcher
    extends BaseSamsungPurchaseFetcher<
            SamsungSKU,
            THSamsungOrderId,
            THSamsungPurchase,
            SamsungException>
    implements THSamsungPurchaseFetcher
{
    @Inject protected THSamsungPurchaseCache thSamsungPurchaseCache;
    @Inject protected THSamsungExceptionFactory samsungExceptionFactory;

    public THBaseSamsungPurchaseFetcher(Context context, int mode)
    {
        super(context, mode);
        DaggerUtils.inject(this);
    }

    @Override protected SamsungPurchaseCache<SamsungSKU, THSamsungOrderId, THSamsungPurchase> getPurchaseCache()
    {
        return thSamsungPurchaseCache;
    }

    @Override protected List<String> getKnownItemGroups()
    {
        List<String> knownGroupIds = new ArrayList<>();
        knownGroupIds.add(THSamsungConstants.IAP_ITEM_GROUP_ID);
        return knownGroupIds;
    }

    @Override protected THSamsungPurchase createPurchase(String groupId, InboxVo inboxVo)
    {
        return new THSamsungPurchase(groupId, inboxVo, null);
    }

    @Override protected SamsungException createException(ErrorVo errorVo)
    {
        return samsungExceptionFactory.create(errorVo);
    }
}
