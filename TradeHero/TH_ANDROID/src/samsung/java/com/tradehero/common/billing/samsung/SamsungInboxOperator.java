package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.exception.SamsungPurchaseFetchException;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import rx.Observable;
import rx.Subscriber;

public class SamsungInboxOperator extends BaseSamsungOperator
        implements Observable.OnSubscribe<InboxVo>
{
    public static final int FIRST_ITEM_NUM = 1;
    public static final String FIRST_DATE = "20140101";

    @NonNull protected final String groupId;

    //<editor-fold desc="Constructors">
    public SamsungInboxOperator(
            @NonNull SamsungIapHelper mIapHelper,
            @NonNull String groupId)
    {
        super(mIapHelper);
        this.groupId = groupId;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super InboxVo> subscriber)
    {
        mIapHelper.getItemInboxList(
                groupId,
                FIRST_ITEM_NUM,
                Integer.MAX_VALUE,
                FIRST_DATE,
                THSamsungConstants.getTodayStringForInbox(),
                (errorVo, inboxList) -> {
                    if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
                    {
                        for (InboxVo inboxVo : inboxList)
                        {
                            subscriber.onNext(inboxVo);
                        }
                        subscriber.onCompleted();
                    }
                    else
                    {
                        subscriber.onError(new SamsungPurchaseFetchException(errorVo, groupId));
                    }
                });
    }
}
