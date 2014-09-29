package com.tradehero.th.api.users.signup;

import com.tradehero.th.api.form.DeviceUserFormDTO;
import com.tradehero.th.api.form.EmailUserFormDTO;
import com.tradehero.th.api.form.FacebookUserFormDTO;
import com.tradehero.th.api.form.LinkedinUserFormDTO;
import com.tradehero.th.api.form.QQUserFormDTO;
import com.tradehero.th.api.form.TwitterUserFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.form.WeiboUserFormDTO;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class LoginSignUpFormDTOFactory
{
    @NotNull private final Provider<LoginSignUpFormEmailDTO> loginSignUpFormEmailDTOProvider;
    @NotNull private final Provider<LoginSignUpFormFacebookDTO> loginSignUpFormFacebookDTOProvider;
    @NotNull private final Provider<LoginSignUpFormLinkedinDTO> loginSignUpFormLinkedinDTOProvider;
    @NotNull private final Provider<LoginSignUpFormQQDTO> loginSignUpFormQQDTOProvider;
    @NotNull private final Provider<LoginSignUpFormTwitterDTO> loginSignUpFormTwitterDTOProvider;
    @NotNull private final Provider<LoginSignUpFormWeiboDTO> loginSignUpFormWeiboDTOProvider;
    @NotNull private final Provider<LoginSignUpFormDeviceDTO> loginSignUpFormDeviceDTOProvider;

    //<editor-fold desc="Constructors">
    @Inject public LoginSignUpFormDTOFactory(
            @NotNull Provider<LoginSignUpFormEmailDTO> loginSignUpFormEmailDTOProvider,
            @NotNull Provider<LoginSignUpFormFacebookDTO> loginSignUpFormFacebookDTOProvider,
            @NotNull Provider<LoginSignUpFormLinkedinDTO> loginSignUpFormLinkedinDTOProvider,
            @NotNull Provider<LoginSignUpFormQQDTO> loginSignUpFormQQDTOProvider,
            @NotNull Provider<LoginSignUpFormTwitterDTO> loginSignUpFormTwitterDTOProvider,
            @NotNull Provider<LoginSignUpFormDeviceDTO> loginSignUpFormDeviceDTOProvider,
            @NotNull Provider<LoginSignUpFormWeiboDTO> loginSignUpFormWeiboDTOProvider)
    {
        this.loginSignUpFormEmailDTOProvider = loginSignUpFormEmailDTOProvider;
        this.loginSignUpFormFacebookDTOProvider = loginSignUpFormFacebookDTOProvider;
        this.loginSignUpFormLinkedinDTOProvider = loginSignUpFormLinkedinDTOProvider;
        this.loginSignUpFormQQDTOProvider = loginSignUpFormQQDTOProvider;
        this.loginSignUpFormTwitterDTOProvider = loginSignUpFormTwitterDTOProvider;
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
        else if (fromForm instanceof FacebookUserFormDTO)
        {
            LoginSignUpFormFacebookDTO facebook = loginSignUpFormFacebookDTOProvider.get();
            facebook.accessToken = ((FacebookUserFormDTO) fromForm).accessToken;
            created = facebook;
        }
        else if (fromForm instanceof LinkedinUserFormDTO)
        {
            LoginSignUpFormLinkedinDTO linkedIn = loginSignUpFormLinkedinDTOProvider.get();
            linkedIn.accessToken = ((LinkedinUserFormDTO) fromForm).accessToken;
            linkedIn.accessTokenSecret = ((LinkedinUserFormDTO) fromForm).accessTokenSecret;
            created = linkedIn;
        }
        else if (fromForm instanceof QQUserFormDTO)
        {
            LoginSignUpFormQQDTO qq = loginSignUpFormQQDTOProvider.get();
            qq.accessToken = ((QQUserFormDTO) fromForm).accessToken;
            qq.openId = ((QQUserFormDTO) fromForm).openid;
            created = qq;
        }
        else if (fromForm instanceof TwitterUserFormDTO)
        {
            LoginSignUpFormTwitterDTO twitter = loginSignUpFormTwitterDTOProvider.get();
            twitter.accessToken = ((TwitterUserFormDTO) fromForm).accessToken;
            twitter.accessTokenSecret = ((TwitterUserFormDTO) fromForm).accessTokenSecret;
            created = twitter;
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
            //weibo.device_access_token = ((DeviceUserFormDTO) fromForm).deviceAccessToken;
            //weibo.deviceAccessToken = ((DeviceUserFormDTO) fromForm).deviceAccessToken;
            created = device;
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + fromForm.getClass());
        }
        return created;
    }
}
