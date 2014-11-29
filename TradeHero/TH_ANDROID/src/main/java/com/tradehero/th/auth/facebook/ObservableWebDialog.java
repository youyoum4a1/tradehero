package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.facebook.widget.WebDialog;
import rx.Observable;

public class ObservableWebDialog
{
    public static Observable<Bundle> create(@NonNull WebDialog webDialog)
    {
        return Observable.create(new WebDialogCompleteListenerOnSubscribe(webDialog));
    }
}
