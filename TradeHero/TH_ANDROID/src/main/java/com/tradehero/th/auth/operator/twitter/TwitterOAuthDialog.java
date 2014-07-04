package com.tradehero.th.auth.operator.twitter;

import android.content.Context;
import com.tradehero.th.auth.OAuthDialog;
import org.jetbrains.annotations.NotNull;

public class TwitterOAuthDialog extends OAuthDialog
{
    private static final String CALLBACK_URL = "twitter-oauth://complete";
    private static final String SERVICE_URL_ID = "api.twitter";

    //<editor-fold desc="Constructors">
    public TwitterOAuthDialog(
            @NotNull final Context context,
            @NotNull String result,
            @NotNull TwitterFlowResultHandler flowResultHandler)
    {
        super(context, result, CALLBACK_URL, SERVICE_URL_ID, flowResultHandler);
    }
    //</editor-fold>
}
