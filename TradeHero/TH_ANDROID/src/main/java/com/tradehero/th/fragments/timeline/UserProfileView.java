package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.util.AttributeSet;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.widget.user.UserProfileCompactView;
import com.tradehero.th.widget.user.UserProfileDetailView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 3:00 PM Copyright (c) TradeHero
 */
public class UserProfileView extends BetterViewAnimator
    implements DTOView<UserProfileDTO>
{
    @InjectView(R.id.user_profile_compact_view) protected UserProfileCompactView userProfileCompactView;
    @InjectView(R.id.user_profile_detail_view) protected UserProfileDetailView userProfileDetailView;

    private UserProfileDTO userProfileDTO;

    //region Constructors
    public UserProfileView(Context context)
    {
        super(context);
    }

    public UserProfileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //endregion

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        userProfileCompactView.display(userProfileDTO);
        userProfileDetailView.display(userProfileDTO);
    }

}
