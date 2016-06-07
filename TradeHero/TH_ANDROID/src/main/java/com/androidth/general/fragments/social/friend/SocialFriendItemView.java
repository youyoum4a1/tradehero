package com.androidth.general.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.androidth.general.api.DTOView;

abstract public class SocialFriendItemView extends LinearLayout
        implements DTOView<SocialFriendListItemDTO>
{
    //<editor-fold desc="Constructors">
    public SocialFriendItemView(Context context)
    {
        super(context);
    }

    public SocialFriendItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

}