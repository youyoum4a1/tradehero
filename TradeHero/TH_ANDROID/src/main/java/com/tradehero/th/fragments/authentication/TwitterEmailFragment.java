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
import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class TwitterEmailFragment extends Fragment
{
    @InjectView(R.id.authentication_twitter_email_txt) EditText twitterEmail;
    @InjectView(R.id.authentication_twitter_email_button) View twitterConfirm;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_twitter_email, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    public Observable<String> obtainEmail()
    {
        return ViewObservable.clicks(twitterConfirm, false)
                .map(new Func1<View, String>()
                {
                    @Override public String call(View view)
                    {
                        return twitterEmail.getText().toString();
                    }
                });
    }
}
