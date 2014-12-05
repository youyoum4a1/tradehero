package com.tradehero.common.social.facebook;

import android.support.annotation.NonNull;
import com.facebook.Session;
import com.facebook.SessionState;
import rx.Observable;

public class ObservableSessionState
{
    public static Observable<SessionState> create(@NonNull Session session)
    {
        return Observable.create(new SessionStateOnSubscribe(session));
    }
}
