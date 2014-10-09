package com.tradehero.th.base;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.misc.exception.THException;

public class THUser
{
    private static void checkNeedForUpgrade(THException error)
    {
        // FIXME CurrentActivityHolder has been removed, refactor THUser!!!
        //if (error.getCode() == ExceptionCode.DoNotRunBelow)
        //{
        //    final Activity currentActivity = activityProvider.get();
        //    alertDialogUtil.get().popWithOkCancelButton(
        //            currentActivity,
        //            R.string.upgrade_needed,
        //            R.string.please_update,
        //            R.string.update_now,
        //            R.string.later,
        //            new DialogInterface.OnClickListener()
        //            {
        //                @Override public void onClick(DialogInterface dialog, int which)
        //                {
        //                    THToast.show(R.string.update_guide);
        //                    if (currentActivity != null)
        //                    {
        //                        marketUtilLazy.get().showAppOnMarket(currentActivity);
        //                        currentActivity.finish();
        //                    }
        //                }
        //            });
        //}
    }

    // FIXME/refactor this can be done inside ApiAuthenticator
    private static void checkNeedToRenewSocialToken(THException error, DTO credentialsDTO)
    {
        throw new RuntimeException("Social token need to be renewed");
    }
}
