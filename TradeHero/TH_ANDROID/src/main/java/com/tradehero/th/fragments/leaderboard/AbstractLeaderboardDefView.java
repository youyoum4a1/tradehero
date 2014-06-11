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
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.models.leaderboard.LeaderboardDefDTOKnowledge;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class AbstractLeaderboardDefView extends RelativeLayout
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected LeaderboardDefDTOKnowledge leaderboardDefDTOKnowledge;

    @InjectView(R.id.leaderboard_def_item_name) TextView leaderboardDefName;
    @InjectView(R.id.leaderboard_def_item_icon) ImageView leaderboardDefIcon;
    @InjectView(R.id.leaderboard_def_item_user_rank) TextView leaderboardDefUserRank;
    @InjectView(R.id.leaderboard_def_item_desc) TextView leaderboardDefDesc;

    protected LeaderboardDefDTO dto;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileRequestTask;

    //<editor-fold desc="Constructors">
    public AbstractLeaderboardDefView(Context context)
    {
        super(context);
    }

    public AbstractLeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractLeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
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
        detachUserProfileTask();
    }

    private void detachUserProfileTask()
    {
        if (userProfileRequestTask != null)
        {
            userProfileRequestTask.setListener(null);
        }
        userProfileRequestTask = null;
    }

    protected void linkWith(LeaderboardDefDTO dto, boolean andDisplay)
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

        List<Integer> iconResIds = leaderboardDefDTOKnowledge.getLeaderboardDefIcon(dto);
        if (iconResIds.size() > 0)
        {
            leaderboardDefIcon.setImageResource(iconResIds.get(0));
            leaderboardDefIcon.setVisibility(VISIBLE);
        }
        else
        {
            leaderboardDefIcon.setVisibility(GONE);
        }

        if (dto.isExchangeRestricted() || dto.isSectorRestricted())
        {
            leaderboardDefDesc.setText(dto.desc);
            leaderboardDefDesc.setVisibility(VISIBLE);
        }
        else
        {
            leaderboardDefDesc.setVisibility(GONE);
        }

        if (dto.id == LeaderboardDefKeyKnowledge.FRIEND_ID)
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
            }
            else
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

}
