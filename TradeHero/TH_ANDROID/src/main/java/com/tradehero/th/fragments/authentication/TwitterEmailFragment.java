package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.TwitterCredentialsDTO;

public class TwitterEmailFragment extends Fragment
{
    private TwitterCredentialsDTO twitterJson;

    @InjectView(R.id.authentication_twitter_email_txt) EditText twitterEmail;

    @OnClick(R.id.authentication_twitter_email_button) void handleTwitterEmailButtonClicked()
    {
        twitterJson.email = twitterEmail.getText().toString();
        THUser.logInAsyncWithJson(twitterJson, createCallbackForTwitterComplementEmail());

        // FIXME/refactor progressDialog from AuthenticationActivity
        //progressDialog.setMessage(String.format(getString(R.string.authentication_connecting_tradehero), "Twitter"));
        //progressDialog.show();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_twitter_email, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    private LogInCallback createCallbackForTwitterComplementEmail()
    {
        return new LogInCallback()
        {
            @Override public void done(UserLoginDTO user, THException ex)
            {
                // FIXME/refactor
                //if (user != null)
                //{
                //    analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Success, AnalyticsConstants.Twitter));
                //    launchDashboard(user);
                //    finish();
                //}
                //else
                //{
                //    THToast.show(ex);
                //}
                //progressDialog.dismiss();
            }

            @Override public void onStart()
            {
                // do nothing for now
            }
        };
    }
}
