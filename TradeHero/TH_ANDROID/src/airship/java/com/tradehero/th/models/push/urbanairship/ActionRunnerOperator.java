package com.tradehero.th.models.push.urbanairship;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.urbanairship.actions.Action;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionCompletionCallback;
import com.urbanairship.actions.ActionResult;
import com.urbanairship.actions.ActionRunner;
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

    @Override public void call(Subscriber<? super ActionResult> subscriber)
    {
        ActionCompletionCallback callback = new ActionCompletionCallback()
        {
            @Override public void onFinish(ActionResult actionResult)
            {
                subscriber.onNext(actionResult);
                subscriber.onCompleted();
            }
        };
        if (action != null)
        {
            ActionRunner.shared().runAction(action, arguments, callback);
        }
        else if (actionName != null)
        {
            ActionRunner.shared().runAction(actionName, arguments, callback);
        }
        else
        {
            subscriber.onError(new IllegalArgumentException("Both action and actionName were null"));
        }
    }
}
