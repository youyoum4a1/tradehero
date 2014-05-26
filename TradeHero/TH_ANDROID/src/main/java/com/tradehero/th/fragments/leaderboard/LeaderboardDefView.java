package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class LeaderboardDefView extends RelativeLayout implements DTOView<LeaderboardDefKey>
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    @InjectView(R.id.leaderboard_def_item_name) TextView leaderboardDefName;
    @InjectView(R.id.leaderboard_def_item_icon) ImageView leaderboardDefIcon;
    @InjectView(R.id.leaderboard_def_item_user_rank) TextView leaderboardDefUserRank;
    @InjectView(R.id.leaderboard_def_item_desc) TextView leaderboardDefDesc;

    private LeaderboardDefDTO dto;

    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileRequestTask;
    private LeaderboardDefKey leaderboardDefKey;

    //<editor-fold desc="Constructors">
    public LeaderboardDefView(Context context)
    {
        super(context);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (currentUserId != null)
        {
            // TODO this is just for getting leaderboard ranking of current user, which is already done by getting user rank from DefDTO, see
            // method @updateRankTitle
            //userProfileRequestTask = userProfileCache.get().getOrFetch(currentUserId.get(), false, userProfileListener);
            //userProfileRequestTask.execute();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (userProfileRequestTask != null)
        {
            userProfileRequestTask.setListener(null);
        }
        userProfileRequestTask = null;
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

        if (dto.id == LeaderboardDefDTO.LEADERBOARD_FRIEND_ID)
        {
            // TODO new background image for android
            //leaderboardDefUserRank.setBackgroundResource(R.drawable.lb_friends_bg);
        }

        updateRankTitle();
    }

    private void updateRankTitle()
    {
        Integer rank = dto.getRank();
        if (rank == null)
        {
            // not a hard coded definition
            if (dto.id > 0)
            {
                leaderboardDefUserRank.setText(getContext().getString(R.string.leaderboard_not_ranked));
            }else
            {
                leaderboardDefUserRank.setText("");
            }
        }
        else
        {
            leaderboardDefUserRank.setText(rank.toString());
        }
        Timber.d("updateRankTitle rank %s for %s result:%s",rank,dto.name,leaderboardDefUserRank.getText().toString());
    }

    private void updateLeaderboardOwnRank(UserProfileDTO userProfileDTO)
    {
        //if (dto != null)
        //{
        //    int leaderboardRank = userProfileDTO.getLeaderboardRanking(dto.getId());
        //    if (leaderboardRank > 0)
        //    {
        //        leaderboardDefUserRank.setText("" + leaderboardRank);
        //    }
        //}
    }

    /**
     * Hardcoded stuff look here: https://github.com/TradeHero/TH_IOS/blob/develop/Code/Models/THLeaderboardDef+Icon.m
     *
     * @return drawable id of leaderboard def icon
     */
    private int getLeaderboardDefIcon()
    {
        switch (dto.id)
        {
            case LeaderboardDefDTO.LEADERBOARD_HERO_ID:
                return R.drawable.icn_lb_heroes;

            case LeaderboardDefDTO.LEADERBOARD_FOLLOWER_ID:
                return R.drawable.icn_lb_followers;

            case LeaderboardDefDTO.LEADERBOARD_FRIEND_ID:
                return R.drawable.leaderboard_friends;

            case LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID:
                return R.drawable.icn_lb_sectors;

            case LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID:
                return R.drawable.icn_lb_exchanges;

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
                return R.drawable.icn_lb_30d;

            case 41:
                return R.drawable.icn_lb_90d;

            case 49:
                return R.drawable.icn_lb_most_skilled;

            case 285:
                return R.drawable.icn_lb_6m;

            default:
                return 0;
        }
    }

    @Override public void display(LeaderboardDefKey dto)
    {
        this.leaderboardDefKey = dto;
        if (leaderboardDefKey != null)
        {
            linkWith(leaderboardDefCache.get().get(leaderboardDefKey), true);
        }
    }
}
