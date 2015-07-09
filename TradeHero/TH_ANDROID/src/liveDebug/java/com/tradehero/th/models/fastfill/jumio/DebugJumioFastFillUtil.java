package com.tradehero.th.models.fastfill.jumio;

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
        return Observable.just(true);
    }
}