package com.tradehero.th.fragments.authentication;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import rx.functions.Action1;

public class OpenDashboardAction implements Action1<Pair<AuthData, UserProfileDTO>>
{
    private final Activity activity;

    public OpenDashboardAction(Activity activity)
    {
        this.activity = activity;
    }

    @Override public void call(Pair<AuthData, UserProfileDTO> authDataUserProfileDTOPair)
    {
        activity.finish();
        activity.startActivity(new Intent(activity, DashboardActivity.class));
    }
}
