package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardUserDTOView extends RelativeLayout implements DTOView<LeaderboardUserDTO>
{
    @Inject protected Picasso picasso;
    private TextView lbmuDisplayName;
    private ImageView lbmuProfilePicture;
    private TextView lbmuId;
    private TextView lbmuHeroQuotient;

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
        lbmuId = (TextView) findViewById(R.id.leaderboard_user_item_id);
        lbmuDisplayName = (TextView) findViewById(R.id.leaderboard_user_item_display_name);
        lbmuProfilePicture = (ImageView) findViewById(R.id.leaderboard_user_item_profile_picture);
        lbmuHeroQuotient = (TextView) findViewById(R.id.leaderboard_user_item_hq);

        DaggerUtils.inject(this);
    }

    @Override public void display(LeaderboardUserDTO dto)
    {
        if (dto == null)
        {
            return;
        }

        lbmuId.setText("" + dto.rank);
        lbmuDisplayName.setText(dto.displayName);
        lbmuHeroQuotient.setText("" + dto.heroQuotient);

        if (dto.picture != null)
        {
            picasso.load(dto.picture).into(lbmuProfilePicture);
        }

    }
}
