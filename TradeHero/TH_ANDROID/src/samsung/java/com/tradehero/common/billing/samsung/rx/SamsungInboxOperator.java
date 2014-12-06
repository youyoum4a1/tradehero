package com.tradehero.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.BaseSamsungOperator;
import com.tradehero.common.billing.samsung.exception.SamsungPurchaseFetchException;
import rx.Observable;
import rx.Subscriber;

public class SamsungInboxOperator extends BaseSamsungOperator
        implements Observable.OnSubscribe<InboxVo>
{
    protected final int startNum;
    protected final int endNum;
    @NonNull protected final String startDate;
    @NonNull protected final String endDate;
    @NonNull protected final String groupId;

    //<editor-fold desc="Constructors">
    public SamsungInboxOperator(
            @NonNull Context context,
            int mode,
            @NonNull InboxListQueryGroup queryGroup)
    {
        this(context,
                mode,
                queryGroup.startNum,
                queryGroup.endNum,
                queryGroup.startDate,
                queryGroup.endDate,
                queryGroup.groupId);
    }

    public SamsungInboxOperator(
            @NonNull Context context,
            int mode,
            int startNum,
            int endNum,
            @NonNull String startDate,
            @NonNull String endDate,
            @NonNull String groupId)
    {
        super(context, mode);
        this.startNum = startNum;
        this.endNum = endNum;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupId = groupId;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super InboxVo> subscriber)
    {
        getSamsungIapHelper().getItemInboxList(
                groupId,
                startNum,
                endNum,
                startDate,
                //THSamsungConstants.getTodayStringForInbox(),
                endDate,
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
