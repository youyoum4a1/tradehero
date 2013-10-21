package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardUserDTOView extends RelativeLayout implements DTOView<LeaderboardUserDTO>
{
    private TextView lbmuDisplayName;
    private ImageView lbmuProfilePicture;

    //<editor-fold desc="Constructors">
    public LeaderboardUserDTOView(Context context)
    {
        super(context);
    }

    public LeaderboardUserDTOView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardUserDTOView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        init();
    }

    private void init()
    {
        lbmuDisplayName = (TextView) findViewById(R.id.leaderboard_user_item_display_name);
        lbmuProfilePicture = (ImageView) findViewById(R.id.leaderboard_user_item_profile_picture);
    }

    @Override public void display(LeaderboardUserDTO dto)
    {
        lbmuDisplayName.setText(dto.displayName);
    }
}
