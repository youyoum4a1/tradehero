package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import com.facebook.widget.WebDialog;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class ObservableWebDialog
{
    public static Observable<Bundle> create(@NotNull WebDialog webDialog)
    {
        return Observable.create(new WebDialogCompleteListenerOnSubscribe(webDialog));
    }
}
