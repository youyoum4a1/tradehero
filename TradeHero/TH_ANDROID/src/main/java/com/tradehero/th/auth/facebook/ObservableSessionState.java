package com.tradehero.th.auth.facebook;

import com.facebook.Session;
import com.facebook.SessionState;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public class ObservableSessionState
{
    public static Observable<SessionState> create(@NotNull Session session)
    {
        return Observable.create(new SessionStateOnSubscribe(session));
    }
}
