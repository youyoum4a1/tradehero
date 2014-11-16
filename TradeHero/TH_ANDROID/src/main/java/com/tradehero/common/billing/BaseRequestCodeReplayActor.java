package com.tradehero.common.billing;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class BaseRequestCodeReplayActor<ResultType> extends BaseRequestCodeActor
{
    protected BehaviorSubject<ResultType> subject;
    protected Observable<ResultType> replayObservable;

    //<editor-fold desc="Constructors">
    protected BaseRequestCodeReplayActor(int requestCode)
    {
        super(requestCode);
        this.subject = BehaviorSubject.create();
        this.replayObservable = subject.replay().publish();
    }
    //</editor-fold>
}
