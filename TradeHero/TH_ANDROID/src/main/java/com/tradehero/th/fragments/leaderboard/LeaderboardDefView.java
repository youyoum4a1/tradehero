package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 10/16/13 Time: 4:19 PM Copyright (c) TradeHero */
public class LeaderboardDefView extends RelativeLayout implements DTOView<LeaderboardDefDTO>
{
    @Inject @Named("CurrentUser") protected UserBaseDTO userBaseDTO;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    private TextView leaderboardDefName;
    private ImageView leaderboardDefIcon;
    private TextView leaderboardDefUserRank;
    private TextView leaderboardDefDesc;

    private LeaderboardDefDTO dto;

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserProfileDTO> userProfileRequestTask;

    //<editor-fold desc="Constructors">
    public LeaderboardDefView(Context context)
    {
        super(context);
        init();
    }

    public LeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        init();
    }

    private void init()
    {
        leaderboardDefName = (TextView) findViewById(R.id.leaderboard_def_item_name);
        leaderboardDefIcon = (ImageView) findViewById(R.id.leaderboard_def_item_icon);
        leaderboardDefUserRank = (TextView) findViewById(R.id.leaderboard_def_item_user_rank);
        leaderboardDefDesc = (TextView) findViewById(R.id.leaderboard_def_item_desc);

        userProfileListener = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                updateLeaderboardOwnRank(value);
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THToast.show("Error getting current user profile");
            }
        };


        if (userBaseDTO != null)
        {
            userProfileRequestTask = userProfileCache.get().getOrFetch(userBaseDTO.getBaseKey(), false, userProfileListener);
            userProfileRequestTask.execute();
        }
    }

    @Override public void display(LeaderboardDefDTO dto)
    {
        linkWith(dto, true);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (userProfileRequestTask != null)
        {
            userProfileRequestTask.forgetListener(true);
        }
    }

    private void linkWith(LeaderboardDefDTO dto, boolean andDisplay)
    {
        this.dto = dto;
        if (dto == null)
        {
            return;
        }

        if (andDisplay)
        {
            display();
        }
    }

    private void display()
    {
        leaderboardDefName.setText(dto.name);

        int leaderboardDefIconResourceId = getLeaderboardDefIcon();
        if (leaderboardDefIconResourceId != 0)
        {
            leaderboardDefIcon.setImageResource(leaderboardDefIconResourceId);
        }
        else
        {
            leaderboardDefIcon.setVisibility(GONE);
        }

        if (dto.isExchangeRestricted() || dto.isSectorRestricted())
        {
            leaderboardDefDesc.setText(dto.desc);
        }
        else
        {
            leaderboardDefDesc.setVisibility(GONE);
        }

        if (dto.getId() == LeaderboardDefDTO.LEADERBOARD_FRIEND_ID)
        {
            // TODO new background image for android
            //leaderboardDefUserRank.setBackgroundResource(R.drawable.lb_friends_bg);
        }
    }

    private void updateLeaderboardOwnRank(UserProfileDTO userProfileDTO)
    {
        if (dto != null)
        {
            int leaderboardRank = userProfileDTO.getLeaderboardRanking(dto.getId());
            if (leaderboardRank > 0)
            {
                leaderboardDefUserRank.setText("" + leaderboardRank);
            }
        }
    }

    /**
     * Hardcoded stuff
     * look here: https://github.com/TradeHero/TH_IOS/blob/develop/Code/Models/THLeaderboardDef+Icon.m
     * @return drawable id of leaderboard def icon
     */
    private int getLeaderboardDefIcon()
    {
        switch (dto.getId())
        {
            case LeaderboardDefDTO.LEADERBOARD_FRIEND_ID:
                return R.drawable.lb_friends_blue;

            case LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID:
                return R.drawable.lb_sector;

            case LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID:
                return R.drawable.lb_exchange;

            case 2:
                return R.drawable.lb_toptraders;

            case 21:
                return R.drawable.lb_cal_11;

            case 22:
                return R.drawable.lb_cal_12;

            case 23:
                return R.drawable.lb_cal_01;

            case 24:
                return R.drawable.lb_cal_02;

            case 25:
                return R.drawable.lb_cal_03;

            case 26:
                return R.drawable.lb_cal_04;

            case 27:
                return R.drawable.lb_cal_05;

            case 28:
                return R.drawable.lb_cal_06;

            case 29:
                return R.drawable.lb_cal_07;

            case 30:
                return R.drawable.lb_cal_08;

            case 31:
                return R.drawable.lb_cal_09;

            case 32:
                return R.drawable.lb_cal_10;

            case 33:
                return R.drawable.lb_cal_11;

            case 34:
                return R.drawable.lb_cal_12;

            case 35:
                return R.drawable.lb_quarter4;

            case 36:
                return R.drawable.lb_quarter1;

            case 37:
                return R.drawable.lb_quarter2;

            case 38:
                return R.drawable.lb_quarter3;

            case 39:
                return R.drawable.lb_quarter4;

            case 40:
                return R.drawable.lb_30days;

            case 41:
                return R.drawable.lb_90days;

            case 49:
                return R.drawable.lb_hq;

            case 285:
                return R.drawable.lb_6mths;

            default:
                return 0;
        }
    }
}
