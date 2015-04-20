package com.tradehero.th.fragments.authentication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.models.push.DeviceTokenHelper;
import com.tradehero.th.utils.DaggerUtils;

import javax.inject.Inject;

abstract public class SignInOrUpFragment extends AuthenticationFragment
{

    abstract protected int getViewId();

    abstract protected int getEmailSignUpViewId();

    @Inject protected DeviceTokenHelper deviceTokenHelper;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(getViewId(), container, false);

        setOnClickListener(view);

        return view;
    }

    public void setOnClickListener(View view)
    {
        view.findViewById(R.id.btn_weibo_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_qq_signin).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_wechat_signin).setOnClickListener(onClickListener);
        view.findViewById(getEmailSignUpViewId()).setOnClickListener(onClickListener);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        showViewForChinese();
    }

    private void showViewForChinese()
    {
        View root = getView();
        root.findViewById(R.id.btn_weibo_signin).setVisibility(View.VISIBLE);
        root.findViewById(R.id.btn_qq_signin).setVisibility(View.VISIBLE);
        root.findViewById(R.id.btn_wechat_signin).setVisibility(View.VISIBLE);
    }

    abstract public AuthenticationMode getAuthenticationMode();
}
