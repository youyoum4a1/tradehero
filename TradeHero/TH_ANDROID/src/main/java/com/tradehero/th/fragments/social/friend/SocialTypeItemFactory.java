package com.tradehero.th.fragments.social.friend;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
        socialList.add(new SocialTypeItemWeibo());
        socialList.add(new SocialTypeItemWechat());
        return socialList;
    }
}
