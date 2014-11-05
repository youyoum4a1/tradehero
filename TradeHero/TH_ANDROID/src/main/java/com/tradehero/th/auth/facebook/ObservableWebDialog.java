package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import com.facebook.widget.WebDialog;
import android.support.annotation.NonNull;
import rx.Observable;

public class ObservableWebDialog
{
    public static Observable<Bundle> create(@NonNull WebDialog webDialog)
    {
        return Observable.create(new WebDialogCompleteListenerOnSubscribe(webDialog));
    }
}
