package com.tradehero.th.models.user.auth;

import android.content.Context;
import com.tradehero.th2.R;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisplayableCredentialsDTO
{
    @NotNull private final Context context;
    @Nullable public final CredentialsDTO credentialsDTO;

    //<editor-fold desc="Constructors">
    public DisplayableCredentialsDTO(
            @NotNull Context context,
            @Nullable CredentialsDTO credentialsDTO)
    {
        super();
        this.context = context;
        this.credentialsDTO = credentialsDTO;
    }
    //</editor-fold>

    @Nullable public String getTypeAndId()
    {
        if (credentialsDTO == null)
        {
            return null;
        }
        else if (credentialsDTO instanceof EmailCredentialsDTO)
        {
            return context.getString(R.string.settings_misc_sign_out_summary,
                    context.getString(R.string.email),
                    ((EmailCredentialsDTO) credentialsDTO).email);
        }
        //else if (credentialsDTO instanceof FacebookCredentialsDTO)
        //{
        //    return context.getString(R.string.settings_misc_sign_out_summary,
        //        context.getString(R.string.facebook),
        //        ((FacebookCredentialsDTO) credentialsDTO).id);
        //}
        else if (credentialsDTO instanceof LinkedinCredentialsDTO)
        {
            return context.getString(R.string.settings_misc_sign_out_summary,
                context.getString(R.string.linkedin),
                ((LinkedinCredentialsDTO) credentialsDTO).consumerKey);
        }
        else if (credentialsDTO instanceof QQCredentialsDTO)
        {
            return context.getString(R.string.settings_misc_sign_out_summary,
                context.getString(R.string.tencent_qq),
                ((QQCredentialsDTO) credentialsDTO).openId);
        }
        else if (credentialsDTO instanceof TwitterCredentialsDTO)
        {
            return context.getString(R.string.settings_misc_sign_out_summary,
                context.getString(R.string.twitter),
                ((TwitterCredentialsDTO) credentialsDTO).id);
        }
        else if (credentialsDTO instanceof WeiboCredentialsDTO)
        {
            return context.getString(R.string.settings_misc_sign_out_summary,
                context.getString(R.string.sina_weibo),
                ((WeiboCredentialsDTO) credentialsDTO).uid);
        }
        return context.getString(R.string.na);
    }
}
