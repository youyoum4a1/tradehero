package com.tradehero.th.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.BaseSamsungPurchaseFetcherRx;
import com.tradehero.common.billing.samsung.rx.InboxListQueryGroup;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import java.util.Collections;
import java.util.List;

public class THBaseSamsungPurchaseFetcherRx
        extends BaseSamsungPurchaseFetcherRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaseFetcherRx
{
    public static final int FIRST_ITEM_NUM = 1;
    public static final String FIRST_DATE = "20140101";

    @NonNull protected final OwnedPortfolioId defaultPortfolioId;

    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaseFetcherRx(
            int requestCode,
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull OwnedPortfolioId defaultPortfolioId)
    {
        super(requestCode, context, mode);
        this.defaultPortfolioId = defaultPortfolioId;
    }
    //</editor-fold>

    @NonNull @Override protected List<InboxListQueryGroup> getInboxListQueryGroups()
    {
        return Collections.singletonList(new InboxListQueryGroup(FIRST_ITEM_NUM,
                Integer.MAX_VALUE,
                THSamsungConstants.IAP_ITEM_GROUP_ID,
                FIRST_DATE,
                THSamsungConstants.getTodayStringForInbox()));
    }

    @NonNull @Override protected THSamsungPurchase createPurchase(@NonNull String groupId, @NonNull InboxVo inboxVo)
    {
        return new THSamsungPurchase(groupId, inboxVo.getJsonString(), defaultPortfolioId);
    }
}
