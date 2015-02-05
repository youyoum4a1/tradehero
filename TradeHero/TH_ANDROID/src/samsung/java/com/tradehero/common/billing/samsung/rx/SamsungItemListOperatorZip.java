package com.tradehero.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.sec.android.iap.lib.vo.ItemVo;
import java.util.List;
import rx.Observable;

public class SamsungItemListOperatorZip
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull private final List<ItemListQueryGroup> queryGroups;

    //<editor-fold desc="Constructors">
    public SamsungItemListOperatorZip(
            @NonNull Context context,
            int mode,
            @NonNull List<ItemListQueryGroup> queryGroups)
    {
        this.context = context;
        this.mode = mode;
        this.queryGroups = queryGroups;
    }
    //</editor-fold>

    @NonNull public Observable<Pair<ItemListQueryGroup, List<ItemVo>>> getItems()
    {
        // We probably need to control when each operator is called
        // Ideally, the next operator should be called only when the previous has completed
        return Observable.zip(
                Observable.from(queryGroups),
                Observable.from(queryGroups).flatMap(queryGroup ->
                        Observable.create(
                                new SamsungItemListOperator(context, mode, queryGroup))),
                Pair::new);
    }
}
