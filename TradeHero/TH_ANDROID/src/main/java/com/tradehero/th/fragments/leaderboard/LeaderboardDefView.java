package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 4:19 PM Copyright (c) TradeHero */
public class LeaderboardDefView extends LinearLayout implements DTOView<LeaderboardDefDTO>
{
    private TextView leaderboardDefName;
    private ImageView leaderboardDefIcon;

    //<editor-fold desc="Constructors">
    public LeaderboardDefView(Context context)
    {
        this(context, null);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
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
        leaderboardDefName = (TextView) findViewById(R.id.leaderboard_def_item_name);
        leaderboardDefIcon = (ImageView) findViewById(R.id.leaderboard_def_item_icon);
    }

    @Override public void display(LeaderboardDefDTO dto)
    {
        leaderboardDefName.setText(dto.name);

        // TODO hardcoded, need to fix on server, returning image?
        if (dto.getId() == LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID)
        {
            leaderboardDefIcon.setImageResource(R.drawable.lb_sector);
        }
        else if (dto.getId() == LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID)
        {
            leaderboardDefIcon.setImageResource(R.drawable.lb_exchange);
        }
    }
}
