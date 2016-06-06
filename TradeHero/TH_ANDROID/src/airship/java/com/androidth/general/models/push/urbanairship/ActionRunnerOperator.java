package com.androidth.general.models.push.urbanairship;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.urbanairship.actions.Action;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionResult;
import com.urbanairship.actions.ActionRunRequest;
import com.urbanairship.actions.ActionRunRequestFactory;
import rx.Observable;
import rx.Subscriber;

public class ActionRunnerOperator implements Observable.OnSubscribe<ActionResult>
{
    @Nullable private final Action action;
    @Nullable private final String actionName;
    @NonNull private final ActionArguments arguments;

    //<editor-fold desc="Constructors">
    public ActionRunnerOperator(
            @SuppressWarnings("NullableProblems") @NonNull Action action,
            @NonNull ActionArguments arguments)
    {
        this.action = action;
        this.actionName = null;
        this.arguments = arguments;
    }

    public ActionRunnerOperator(
            @SuppressWarnings("NullableProblems") @NonNull String actionName,
            @NonNull ActionArguments arguments)
    {
        this.action = null;
        this.actionName = actionName;
        this.arguments = arguments;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super ActionResult> subscriber)
    {
        if (action != null)
        {
            ActionRunRequest request = ActionRunRequest.createRequest(action);
            request.setMetadata(arguments.getMetadata());
            subscriber.onNext(request.runSync());
            subscriber.onCompleted();
        }
        else if (actionName != null)
        {
            ActionRunRequest request = new ActionRunRequestFactory().createActionRequest(actionName);
            request.setMetadata(arguments.getMetadata());
            subscriber.onNext(request.runSync());
            subscriber.onCompleted();
        }
        else
        {
            subscriber.onError(new IllegalArgumentException("Both action and actionName were null"));
        }
    }
}
