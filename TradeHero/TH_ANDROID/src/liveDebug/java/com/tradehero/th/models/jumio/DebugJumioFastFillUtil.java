package com.tradehero.th.models.jumio;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class DebugJumioFastFillUtil extends JumioFastFillUtil
{
    @Inject public DebugJumioFastFillUtil(@NonNull CurrentUserId currentUserId)
    {
        super(currentUserId);
    }

    @NonNull @Override public Observable<Boolean> isAvailable(@NonNull final Activity activity)
    {
        return super.isAvailable(activity)
                .flatMap(new Func1<Boolean, Observable<Boolean>>()
                {
                    @Override public Observable<Boolean> call(final Boolean fastFillAvailable)
                    {
                        if (!fastFillAvailable)
                        {
                            return AlertDialogRxUtil.build(activity)
                                    .setTitle("Jumio FastFill is not available. Fake it?")
                                    .setPositiveButton("Yes")
                                    .setNegativeButton("No")
                                    .build()
                                    .map(new Func1<OnDialogClickEvent, Boolean>()
                                    {
                                        @Override public Boolean call(OnDialogClickEvent clickEvent)
                                        {
                                            return clickEvent.isPositive() || fastFillAvailable;
                                        }
                                    });
                        }
                        else
                        {
                            return Observable.just(true);
                        }
                    }
                });
    }
}
