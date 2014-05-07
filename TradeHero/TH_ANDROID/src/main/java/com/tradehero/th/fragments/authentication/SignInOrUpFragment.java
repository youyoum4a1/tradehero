package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.MetaHelper;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.utils.DaggerUtils;
import timber.log.Timber;


abstract public class SignInOrUpFragment extends AuthenticationFragment
{
    abstract protected int getViewId();
    abstract protected int getEmailSignUpViewId();

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewId(), container, false);

        view.findViewById(R.id.btn_facebook_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_twitter_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_linkedin_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_weibo_signin).setOnClickListener(onClickListener);
        view.findViewById(getEmailSignUpViewId()).setOnClickListener(onClickListener);
        view.findViewById(R.id.txt_term_of_service_signin).setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkLocale();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void checkLocale() {
        boolean isChineseLocale = DeviceTokenHelper.isChineseVersion();
        String language = MetaHelper.getLanguage(getActivity());
        Timber.d("language %s",language);
        if (isChineseLocale) {
            showViewForChinese();
        }
    }

    private void showViewForChinese() {
        View root = getView();
        root.findViewById(R.id.btn_facebook_signin).setVisibility(View.GONE);
        root.findViewById(R.id.btn_twitter_signin).setVisibility(View.GONE);
        root.findViewById(R.id.btn_linkedin_signin).setVisibility(View.VISIBLE);
        root.findViewById(R.id.btn_weibo_signin).setVisibility(View.VISIBLE);
    }


    abstract public AuthenticationMode getAuthenticationMode();
}
