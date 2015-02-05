package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.def.ConnectedLeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.leaderboard.LeaderboardDefDTOKnowledge;
import java.util.List;
import javax.inject.Inject;
import rx.Subscription;
import timber.log.Timber;

public class LeaderboardDefView extends RelativeLayout
        implements DTOView<LeaderboardDefDTO>
{
    @Inject protected LeaderboardDefDTOKnowledge leaderboardDefDTOKnowledge;

    @InjectView(R.id.leaderboard_def_item_icon_container) View leaderboardDefIconContainer;
    @InjectView(R.id.leaderboard_def_item_icon) ImageView leaderboardDefIcon;
    @InjectView(R.id.leaderboard_def_item_icon_2) @Optional ImageView leaderboardDefIcon2;
    @InjectView(R.id.leaderboard_def_item_icon_3) @Optional ImageView leaderboardDefIcon3;
    @InjectView(R.id.leaderboard_def_item_name) TextView leaderboardDefName;
    @InjectView(R.id.leaderboard_def_item_desc) TextView leaderboardDefDesc;
    @InjectView(R.id.leaderboard_def_item_action_icon) ImageView actionIcon;
    @InjectView(R.id.leaderboard_def_item_user_rank) TextView leaderboardDefUserRank;
    @InjectView(R.id.leaderboard_def_utem_rank_wrapper) View leaderboardDefUserRankWrapper;

    protected LeaderboardDefDTO dto;
    private Subscription fetchUserRankingSubscription;

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
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        detachFetchUserRanking();
        super.onDetachedFromWindow();
    }

    private void detachFetchUserRanking()
    {
        if (fetchUserRankingSubscription != null)
        {
            fetchUserRankingSubscription.unsubscribe();
        }
    }

    @Override public void display(LeaderboardDefDTO leaderboardDefDTO)
    {
        dto = leaderboardDefDTO;
        fetchUserRank();

        if (leaderboardDefDTO == null)
        {
            return;
        }

        display();
    }

    private void fetchUserRank()
    {
        detachFetchUserRanking();
/*        if (dto.id > 0)
        {
            fetchUserRankingSubscription = leaderboardCache.get(new UserOnLeaderboardKey(dto.id, currentUserId.get()))
                    .doOnError(toastOnError)
                    .map(i -> i.second.users.get(0))
                    .first()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showUserBestROI, throwable -> {
                        // Do nothing
                    });
        }
        else
        {
            leaderboardDefUserRankWrapper.setVisibility(GONE);
        }*/
        leaderboardDefUserRankWrapper.setVisibility(GONE);
    }

    private void display()
    {
        leaderboardDefName.setText(dto.name);
        displayIcon();
        if (dto instanceof ConnectedLeaderboardDefDTO)
        {
            showAsConnected((ConnectedLeaderboardDefDTO) dto);
        }
        else
        {
            showAsRegular();
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
    }

    protected void showAsConnected(ConnectedLeaderboardDefDTO connected)
    {
        Integer bannerResId = connected.bannerResId;
        if (bannerResId != null)
        {
            try
            {
                showAsBanner(bannerResId);
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, "Setting background");
                showAsRegular();
            }
        }
        else
        {
            showAsRegular();
        }
    }

    protected void showAsRegular()
    {
        setBackgroundResource(R.drawable.basic_white_selector);
        leaderboardDefIconContainer.setVisibility(VISIBLE);
        leaderboardDefName.setVisibility(VISIBLE);
        actionIcon.setVisibility(VISIBLE);
    }

    protected void showAsBanner(int bannerResId)
    {
        setBackgroundResource(bannerResId);
        leaderboardDefIconContainer.setVisibility(INVISIBLE);
        leaderboardDefName.setVisibility(INVISIBLE);
        actionIcon.setVisibility(INVISIBLE);
    }

    protected void displayIcon()
    {
        List<Integer> iconResIds = leaderboardDefDTOKnowledge.getLeaderboardDefIcon(dto);
        if (iconResIds.size() > 0)
        {
            try
            {
                leaderboardDefIconContainer.setVisibility(VISIBLE);
                leaderboardDefIcon.setImageResource(iconResIds.get(0));
                if (leaderboardDefIcon2 != null)
                {
                    if (iconResIds.size() > 1)
                    {
                        leaderboardDefIcon2.setVisibility(VISIBLE);
                        leaderboardDefIcon2.setImageResource(iconResIds.get(1));
                    }
                    else
                    {
                        leaderboardDefIcon2.setVisibility(GONE);
                    }
                }

                if (leaderboardDefIcon3 != null)
                {
                    if (iconResIds.size() > 2)
                    {
                        leaderboardDefIcon3.setVisibility(VISIBLE);
                        leaderboardDefIcon3.setImageResource(iconResIds.get(2));
                    }
                    else
                    {
                        leaderboardDefIcon3.setVisibility(GONE);
                    }
                }
            }
            catch (OutOfMemoryError e)
            {
                leaderboardDefIconContainer.setVisibility(GONE);
            }
        }
        else
        {
            leaderboardDefIconContainer.setVisibility(GONE);
        }
    }
}
