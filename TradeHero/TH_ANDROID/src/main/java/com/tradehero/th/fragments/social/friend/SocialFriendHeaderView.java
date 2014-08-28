package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class SocialFriendHeaderView extends SocialFriendItemView
{
    @InjectView(R.id.social_friend_headline) TextView headLine;

    private SocialFriendListItemHeaderDTO socialFriendListItemHeaderDTO;

    //<editor-fold desc="Constructors">
    public SocialFriendHeaderView(Context context)
    {
        super(context);
    }

    public SocialFriendHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public void display(SocialFriendListItemDTO dto)
    {
        if(dto instanceof SocialFriendListItemHeaderDTO)
        {
            this.socialFriendListItemHeaderDTO = (SocialFriendListItemHeaderDTO) dto;
            displayHeadLine();
        }
    }

    private void displayHeadLine()
    {
        headLine.setText(socialFriendListItemHeaderDTO.header);
    }
}
