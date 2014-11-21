package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SocialTypeItemFactory
{
    //<editor-fold desc="Constructors">
    @Inject public SocialTypeItemFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public List<SocialTypeItem> getSocialTypeList()
    {
        List<SocialTypeItem> socialList = new ArrayList<>();

        socialList.add(new SocialTypeItemFacebook());
        socialList.add(new SocialTypeItemTwitter());
        socialList.add(new SocialTypeItemLinkedin());
        socialList.add(new SocialTypeItemWeibo());
        socialList.add(new SocialTypeItemWechat());

        return socialList;
    }
}
