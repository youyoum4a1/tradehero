package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardUserRankItemView extends RelativeLayout
        implements DTOView<LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper>,View.OnClickListener
{
    @Inject protected Picasso picasso;
    @Inject protected Lazy<PositionCache> positionCache;

    private TextView lbmuDisplayName;
    private ImageView lbmuProfilePicture;
    private TextView lbmuPosition;
    private TextView lbmuHeroQuotient;
    private ImageView lbmuPositionInfo;
    private PositionDTO position;

    private LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper leaderboardItem;
    private TextView lbmuPl;
    private TextView lbmuRoi;

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
        super.onFinishInflate();

        DaggerUtils.inject(this);
        initViews();
    }

    private void initViews()
    {
        lbmuPosition = (TextView) findViewById(R.id.leaderboard_user_item_position);
        lbmuDisplayName = (TextView) findViewById(R.id.leaderboard_user_item_display_name);
        lbmuProfilePicture = (ImageView) findViewById(R.id.leaderboard_user_item_profile_picture);
        lbmuHeroQuotient = (TextView) findViewById(R.id.leaderboard_user_item_hq);
        lbmuPositionInfo = (ImageView) findViewById(R.id.leaderboard_user_item_info);

        if (lbmuPositionInfo != null)
        {
            lbmuPositionInfo.setOnClickListener(this);
        }

        // for expanding part
        lbmuPl = (TextView) findViewById(R.id.lbmu_pl);
        lbmuRoi = (TextView) findViewById(R.id.lbmu_roi);
    }

    @Override public void display(LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper expandableItem)
    {
        linkWith(expandableItem, true);
    }

    private void linkWith(LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper expandableItem, boolean andDisplay)
    {
        this.leaderboardItem = expandableItem;
        if (leaderboardItem != null)
        {
            //this.position = positionCache.get().get(new OwnedPositionId());
        }

        if (andDisplay)
        {
            display();
        }
    }

    private void display()
    {
        if (leaderboardItem == null || leaderboardItem.getModel() == null)
        {
            return;
        }

        displayTopSection();
        displayExpandableSection();
    }

    private void displayTopSection()
    {
        LeaderboardUserRankDTO dto = leaderboardItem.getModel();

        lbmuPosition.setText("" + leaderboardItem.getPosition());
        lbmuDisplayName.setText(dto.displayName);
        lbmuHeroQuotient.setText(dto.getHeroQuotientFormatted());

        if (dto.picture != null)
        {
            picasso.load(dto.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(lbmuProfilePicture);
        }
    }

    private void displayExpandableSection()
    {
        LeaderboardUserRankDTO dto = leaderboardItem.getModel();
        //lbmuPl.setText(dto.ge);
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.leaderboard_user_item_info:
                break;
        }
    }
}
