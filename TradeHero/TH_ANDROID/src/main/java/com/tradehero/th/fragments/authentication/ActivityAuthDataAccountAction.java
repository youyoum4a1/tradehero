package com.tradehero.th.fragments.authentication;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import javax.inject.Inject;

import static com.tradehero.th.utils.Constants.Auth.PARAM_ACCOUNT_TYPE;

public class ActivityAuthDataAccountAction extends AuthDataAccountAction
{
    @NonNull private final Activity activity;

    //<editor-fold desc="Constructors">
    @Inject public ActivityAuthDataAccountAction(@NonNull Activity activity)
    {
        super(activity);
        this.activity = activity;
    }

    @Override public void call(@NonNull Pair<AuthData, UserProfileDTO> pair)
    {
        super.call(pair);
        finishAuthentication(pair);
    }

    private void finishAuthentication(@NonNull Pair<AuthData, UserProfileDTO> pair)
    {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, pair.second.email);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, pair.first.getTHToken());
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, PARAM_ACCOUNT_TYPE);
        activity.setResult(Activity.RESULT_OK, intent);
    }
}
