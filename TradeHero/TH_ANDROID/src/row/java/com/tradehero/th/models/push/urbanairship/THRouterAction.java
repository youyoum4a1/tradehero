package com.tradehero.th.models.push.urbanairship;

import android.support.annotation.NonNull;
import com.tradehero.th.utils.route.THRouter;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionResult;
import com.urbanairship.actions.DeepLinkAction;

public class THRouterAction extends DeepLinkAction
{
    @NonNull private final THRouter router;

    //<editor-fold desc="Constructors">
    public THRouterAction(@NonNull THRouter router)
    {
        this.router = router;
    }
    //</editor-fold>

    @Override public ActionResult perform(String actionName, ActionArguments arguments)
    {
        String value;
        Exception caught = null;
        try
        {
            router.open((String) arguments.getValue());
            return ActionResult.newEmptyResult();
        } catch (Exception e)
        {
            return ActionResult.newErrorResult(e);
        }
    }
}
