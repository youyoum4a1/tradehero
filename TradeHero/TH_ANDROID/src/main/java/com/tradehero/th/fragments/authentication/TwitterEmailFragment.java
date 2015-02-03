package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import rx.Observable;
import rx.android.view.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class TwitterEmailFragment extends Fragment
{
    @InjectView(R.id.authentication_twitter_email_txt) EditText twitterEmail;
    @InjectView(R.id.authentication_twitter_email_button) View twitterConfirm;

    @NonNull private PublishSubject<String> loginRequestSubject = PublishSubject.create();

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_twitter_email, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        ViewObservable.clicks(twitterConfirm, false)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(view1 -> twitterEmail.getText().toString())
                .subscribe(loginRequestSubject);
    }

    @Override public void onDetach()
    {
        loginRequestSubject.onCompleted();
        loginRequestSubject = PublishSubject.create();
        super.onDetach();
    }

    @NonNull public Observable<String> obtainEmail()
    {
        return loginRequestSubject.asObservable();
    }
}
