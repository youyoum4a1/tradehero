package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardUserRankItemView extends RelativeLayout
        implements DTOView<LeaderboardListAdapter.ExpandableLeaderboardUserRankItem>
{
    @Inject protected Picasso picasso;
    private TextView lbmuDisplayName;
    private ImageView lbmuProfilePicture;
    private TextView lbmuId;
    private TextView lbmuHeroQuotient;

    //<editor-fold desc="Constructors">
    public LeaderboardUserRankItemView(Context context)
    {
        super(context);
    }

    public LeaderboardUserRankItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardUserRankItemView(Context context, AttributeSet attrs, int defStyle)
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

    @Override public void display(LeaderboardListAdapter.ExpandableLeaderboardUserRankItem dtoWrapper)
    {
        if (dtoWrapper == null || dtoWrapper.getModel() == null)
        {
            return;
        }
        LeaderboardUserRankDTO dto = dtoWrapper.getModel();

        lbmuId.setText("" + dto.rank);
        lbmuDisplayName.setText(dto.displayName);
        lbmuHeroQuotient.setText(dto.getHeroQuotientFormatted());

        if (dto.picture != null)
        {
            picasso.load(dto.picture).into(lbmuProfilePicture);
        }

    }
}
