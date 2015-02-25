package com.tradehero.chinabuild.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.MiddleLogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.QQUtils;
import com.tradehero.th.utils.WeiboUtils;
import dagger.Lazy;

import javax.inject.Inject;

public class MySocialFragment extends DashboardFragment implements View.OnClickListener
{
    @InjectView(R.id.btn_weibo_signin) LinearLayout mWeiboLayout;
    @InjectView(R.id.btn_qq_signin) LinearLayout mQQLayout;
    @InjectView(R.id.weibo_mark) ImageView mWeiboMark;
    @InjectView(R.id.qq_mark) ImageView mQQMark;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<LinkedInUtils> linkedInUtils;
    @Inject Lazy<QQUtils> QQUtilsLazy;
    @Inject Lazy<WeiboUtils> WeiboUtilsLazy;
    @Inject SocialServiceWrapper socialServiceWrapper;
    protected MiddleLogInCallback middleSocialConnectLogInCallback;
    protected MiddleCallback<UserProfileDTO> middleCallbackDisconnect;
    protected MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.settings_my_social);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_my_social_fragment_layout, container, false);
        ButterKnife.inject(this, view);
        mWeiboLayout.setOnClickListener(this);
        mQQLayout.setOnClickListener(this);
        updateView();
        return view;
    }

    private void updateView()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            mWeiboMark.setImageResource(userProfileDTO.wbLinked ? R.drawable.register_duihao
                    : R.drawable.register_duihao_cancel);
            mQQMark.setImageResource(userProfileDTO.qqLinked ? R.drawable.register_duihao
                    : R.drawable.register_duihao_cancel);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @Override public void onClick(View view)
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO == null)
        {
            return;
        }
        progressDialogUtil.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_tradehero, getString(R.string.linkedin)));
        switch (view.getId())
        {
            case R.id.btn_weibo_signin:
                if (userProfileDTO.wbLinked)
                {
                    detachMiddleServerDisconnectCallback();
                    middleCallbackDisconnect = socialServiceWrapper.disconnect(
                            currentUserId.toUserBaseKey(),
                            new SocialNetworkFormDTO(SocialNetworkEnum.WB),
                            new ServerUnlinkingCallback());
                }
                else
                {
                    detachMiddleSocialConnectLogInCallback();
                    middleSocialConnectLogInCallback = createMiddleSocialConnectLogInCallback();
                    WeiboUtilsLazy.get().logIn(getActivity(), middleSocialConnectLogInCallback);
                }
                break;
            case R.id.btn_qq_signin:
                if (userProfileDTO.qqLinked)
                {
                    detachMiddleServerDisconnectCallback();
                    middleCallbackDisconnect = socialServiceWrapper.disconnect(
                            currentUserId.toUserBaseKey(),
                            new SocialNetworkFormDTO(SocialNetworkEnum.QQ),
                            new ServerUnlinkingCallback());
                }
                else
                {
                    detachMiddleSocialConnectLogInCallback();
                    middleSocialConnectLogInCallback = createMiddleSocialConnectLogInCallback();
                    QQUtilsLazy.get().logIn(getActivity(), middleSocialConnectLogInCallback);
                }
                break;
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        progressDialogUtil.dismiss(getActivity());
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    protected void detachMiddleSocialConnectLogInCallback()
    {
        if (middleSocialConnectLogInCallback != null)
        {
            middleSocialConnectLogInCallback.setInnerCallback(null);
        }
    }

    protected MiddleLogInCallback createMiddleSocialConnectLogInCallback()
    {
        return new MiddleLogInCallback(new SocialConnectLogInCallback());
    }

    protected class SocialConnectLogInCallback extends LogInCallback
    {
        @Override public void done(UserLoginDTO user, THException ex)
        {
            progressDialogUtil.dismiss(getActivity());
        }

        @Override public void onStart()
        {
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            reportConnectToServer(json);
            return false;
        }
    }

    protected void reportConnectToServer(JSONCredentials json)
    {
        detachMiddleSocialConnectLogInCallback();
        middleCallbackUpdateUserProfile = socialServiceWrapper.connect(
                currentUserId.toUserBaseKey(),
                UserFormFactory.create(json),
                new UserProfileUpdateCallback());
    }

    protected class UserProfileUpdateCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            updateView();
            userProfileCache.put(currentUserId.toUserBaseKey(), userProfileDTO);
            THToast.show(R.string.bind_success);
        }

        @Override protected void failure(THException ex)
        {
            progressDialogUtil.dismiss(getActivity());
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialogUtil.dismiss(getActivity());
        }
    }

    protected void detachMiddleServerDisconnectCallback()
    {
        if (middleCallbackDisconnect != null)
        {
            middleCallbackDisconnect.setPrimaryCallback(null);
        }
    }

    protected class ServerUnlinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            updateView();
            THToast.show(R.string.unbind_success);
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            progressDialogUtil.dismiss(getActivity());
        }
    }
}
