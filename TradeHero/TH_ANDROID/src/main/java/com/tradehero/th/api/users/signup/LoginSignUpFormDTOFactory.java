package com.tradehero.th.api.users.signup;

import com.tradehero.th.api.form.*;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class LoginSignUpFormDTOFactory
{
    @NotNull private final Provider<LoginSignUpFormEmailDTO> loginSignUpFormEmailDTOProvider;
    @NotNull private final Provider<LoginSignUpFormQQDTO> loginSignUpFormQQDTOProvider;
    @NotNull private final Provider<LoginSignUpFormWeChatDTO> loginSignUpFormWechatDTOProvider;
    @NotNull private final Provider<LoginSignUpFormWeiboDTO> loginSignUpFormWeiboDTOProvider;
    @NotNull private final Provider<LoginSignUpFormDeviceDTO> loginSignUpFormDeviceDTOProvider;

    //<editor-fold desc="Constructors">
    @Inject public LoginSignUpFormDTOFactory(
            @NotNull Provider<LoginSignUpFormEmailDTO> loginSignUpFormEmailDTOProvider,
            @NotNull Provider<LoginSignUpFormQQDTO> loginSignUpFormQQDTOProvider,
            @NotNull Provider<LoginSignUpFormWeChatDTO> loginSignUpFormWechatDTOProvider,
            @NotNull Provider<LoginSignUpFormDeviceDTO> loginSignUpFormDeviceDTOProvider,
            @NotNull Provider<LoginSignUpFormWeiboDTO> loginSignUpFormWeiboDTOProvider)
    {
        this.loginSignUpFormEmailDTOProvider = loginSignUpFormEmailDTOProvider;
        this.loginSignUpFormQQDTOProvider = loginSignUpFormQQDTOProvider;
        this.loginSignUpFormWechatDTOProvider = loginSignUpFormWechatDTOProvider;
        this.loginSignUpFormWeiboDTOProvider = loginSignUpFormWeiboDTOProvider;
        this.loginSignUpFormDeviceDTOProvider = loginSignUpFormDeviceDTOProvider;
    }
    //</editor-fold>

    @NotNull public LoginSignUpFormDTO create(@NotNull UserFormDTO fromForm)
    {
        LoginSignUpFormDTO created;
        if (fromForm instanceof EmailUserFormDTO)
        {
            LoginSignUpFormEmailDTO email = loginSignUpFormEmailDTOProvider.get();
            email.isEmailLogin = true;
            created = email;
        }
        else if (fromForm instanceof QQUserFormDTO)
        {
            LoginSignUpFormQQDTO qq = loginSignUpFormQQDTOProvider.get();
            qq.accessToken = ((QQUserFormDTO) fromForm).accessToken;
            qq.openId = ((QQUserFormDTO) fromForm).openid;
            created = qq;
        }
        else if (fromForm instanceof WechatUserFormDTO)
        {
            LoginSignUpFormWeChatDTO wechat = loginSignUpFormWechatDTOProvider.get();
            wechat.accessToken = ((WechatUserFormDTO) fromForm).accessToken;
            wechat.openId = ((WechatUserFormDTO) fromForm).openid;
            created = wechat;
        }
        else if (fromForm instanceof WeiboUserFormDTO)
        {
            LoginSignUpFormWeiboDTO weibo = loginSignUpFormWeiboDTOProvider.get();
            weibo.accessToken = ((WeiboUserFormDTO) fromForm).accessToken;
            created = weibo;
        }
        else if (fromForm instanceof DeviceUserFormDTO)
        {
            LoginSignUpFormDeviceDTO device = loginSignUpFormDeviceDTOProvider.get();
            created = device;
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + fromForm.getClass());
        }
        return created;
    }
}
