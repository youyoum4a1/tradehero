package com.tradehero.th.fragments.social.friend;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SocialTypeItemFactory
{
    //<editor-fold desc="Constructors">
    @Inject public SocialTypeItemFactory()
    {
        super();
    }
    //</editor-fold>

    @NotNull public List<SocialTypeItem> getSocialTypeList()
    {
        List<SocialTypeItem> socialList = new ArrayList<>();

        socialList.add(new SocialTypeItemFacebook());
        socialList.add(new SocialTypeItemTwitter());
        socialList.add(new SocialTypeItemLinkedin());
        socialList.add(new SocialTypeItemWeibo());

        return socialList;
    }
}
