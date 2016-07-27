package com.androidth.general.fragments.social.friend;

import android.support.annotation.NonNull;

import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;

import java.util.ArrayList;
import java.util.List;

public class SocialTypeItemFactory
{
    @NonNull public static List<SocialTypeItem> getSocialTypeList()
    {
        List<SocialTypeItem> socialList = new ArrayList<>();

        socialList.add(new SocialTypeItemFacebook());
        //socialList.add(new SocialTypeItemTwitter());
        //socialList.add(new SocialTypeItemLinkedin());
        socialList.add(new SocialTypeItemWeibo());
        socialList.add(new SocialTypeItemWechat());

        SocialTypeItem emailItem = new SocialTypeItem(R.drawable.accounts_glyph_email_default,
                R.string.invite_from_email,
                R.drawable.email_selector,
                SocialNetworkEnum.EMAIL);

        socialList.add(emailItem);

        SocialTypeItem smsItem = new SocialTypeItem(R.drawable.accounts_glyph_email_ok,
                R.string.invite_from_sms,
                R.drawable.sms_selector,
                SocialNetworkEnum.EMAIL);
        
        socialList.add(smsItem);

        return socialList;
    }
}
