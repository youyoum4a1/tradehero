package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import timber.log.Timber;

public class LeaderboardMarkUserOwnRankingView extends LeaderboardMarkUserItemView
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserOwnRankingView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public LeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void handleOpenProfileButtonClicked()
    {
        if ((viewDTO == null) && (currentUserProfileDTO == null))
        {
            Timber.e(new Exception(), "No View DTO when trying to open profile");
            return;
        }

        int userId = viewDTO != null ? viewDTO.currentUserId.get() : currentUserProfileDTO.id;

        Bundle bundle = new Bundle();
        UserBaseKey userToSee = new UserBaseKey(userId);
        thRouter.save(bundle, userToSee);

        navigator.pushFragment(MeTimelineFragment.class, bundle);
    }
}
