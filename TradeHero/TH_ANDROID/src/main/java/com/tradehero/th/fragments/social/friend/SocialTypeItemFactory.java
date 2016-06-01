package com.ayondo.academy.fragments.social.friend;

import android.support.annotation.NonNull;
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

        return socialList;
    }
}
