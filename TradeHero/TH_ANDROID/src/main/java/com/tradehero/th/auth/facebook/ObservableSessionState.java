package com.tradehero.th.auth.facebook;

import com.facebook.Session;
import com.facebook.SessionState;
import android.support.annotation.NonNull;
import rx.Observable;

public class ObservableSessionState
{
    public static Observable<SessionState> create(@NonNull Session session)
    {
        return Observable.create(new SessionStateOnSubscribe(session));
    }
}
