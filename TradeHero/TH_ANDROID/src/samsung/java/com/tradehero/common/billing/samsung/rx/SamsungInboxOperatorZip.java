package com.tradehero.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.sec.android.iap.lib.vo.InboxVo;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class SamsungInboxOperatorZip
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull private final List<InboxListQueryGroup> queryGroups;

    //<editor-fold desc="Constructors">
    public SamsungInboxOperatorZip(
            @NonNull Context context,
            int mode,
            @NonNull List<InboxListQueryGroup> queryGroups)
    {
        this.context = context;
        this.mode = mode;
        this.queryGroups = queryGroups;
    }
    //</editor-fold>

    @NonNull public Observable<Pair<InboxListQueryGroup, Observable<InboxVo>>> getInboxItems()
    {
        // We probably need to control when each operator is called
        // Ideally, the next operator should be called only when the previous has completed
        return Observable.zip(
                Observable.from(queryGroups),
                Observable.from(queryGroups).map(new Func1<InboxListQueryGroup, Observable<InboxVo>>()
                {
                    @Override public Observable<InboxVo> call(InboxListQueryGroup queryGroup)
                    {
                        return Observable.create(
                                new SamsungInboxOperator(context, mode, queryGroup));
                    }
                }),
                new Func2<InboxListQueryGroup, Observable<InboxVo>, Pair<InboxListQueryGroup, Observable<InboxVo>>>()
                {
                    @Override public Pair<InboxListQueryGroup, Observable<InboxVo>> call(InboxListQueryGroup t1,
                            Observable<InboxVo> t2)
                    {
                        return new Pair<InboxListQueryGroup, Observable<InboxVo>>(t1, t2);
                    }
                });
    }
}
