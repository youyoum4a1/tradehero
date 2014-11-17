package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.samsung.exception.SamsungItemListException;
import rx.Observable;
import rx.Subscriber;

public class SamsungItemListOperator extends BaseSamsungOperator
    implements Observable.OnSubscribe<ItemVo>
{
    public static final int FIRST_ITEM_NUM = 1;

    @NonNull protected final String groupId;
    protected final int mode;

    //<editor-fold desc="Constructors">
    public SamsungItemListOperator(
            @NonNull SamsungIapHelper mIapHelper,
            @NonNull String groupId,
            int mode)
    {
        super(mIapHelper);
        this.groupId = groupId;
        this.mode = mode;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super ItemVo> subscriber)
    {
        mIapHelper.getItemList(
                groupId,
                FIRST_ITEM_NUM, Integer.MAX_VALUE,
                SamsungIapHelper.ITEM_TYPE_ALL,
                mode,
                (errorVo, itemList) -> {
                    if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
                    {
                        for (ItemVo itemVo : itemList)
                        {
                            subscriber.onNext(itemVo);
                        }
                        subscriber.onCompleted();
                    }
                    else
                    {
                        subscriber.onError(new SamsungItemListException(errorVo, groupId, mode));
                    }
                });
    }
}
