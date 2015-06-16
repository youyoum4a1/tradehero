package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.number.THSignedNumber;

public class CompetitionLeaderboardMarkUserOwnRankingView extends CompetitionLeaderboardMarkUserItemView
{
    @InjectView(R.id.competition_own_ranking_info_container) ViewGroup infoButtonContainer;
    @InjectView(R.id.competition_own_ranking_info_text) TextView infoText;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public CompetitionLeaderboardMarkUserOwnRankingView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionLeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public CompetitionLeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public void display(@NonNull CompetitionLeaderboardItemDisplayDto parentViewDTO)
    {
        super.display(parentViewDTO);
        if (parentViewDTO instanceof CompetitionLeaderboardOwnRankingItemDisplayDto)
        {
            CompetitionLeaderboardOwnRankingItemDisplayDto viewDTO = (CompetitionLeaderboardOwnRankingItemDisplayDto) parentViewDTO;

            if (infoButtonContainer != null)
            {
                infoButtonContainer.setVisibility(viewDTO.infoButtonContainerVisibility);
            }
            if (infoText != null)
            {
                infoText.setText(viewDTO.infoText);
            }
        }
    }

    public void displayUserIsNotRanked(@Nullable UserProfileDTO currentUserProfileDTO)
    {
        //super.displayUserIsNotRanked(currentUserProfileDTO);
        //
        //String rule = getContext().getString(R.string.leaderboard_see_competition_rules);
        //
        //CharacterStyle textColorSpan = createTextColorSpan();
        //CharacterStyle clickableSpan = createClickableSpan();
        //
        //Spannable span = new SpannableString(rule);
        //span.setSpan(clickableSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //span.setSpan(textColorSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //lbmuRoi.setMovementMethod(LinkMovementMethod.getInstance());
        //lbmuRoi.setText(span);
        //lbmuRoi.setBackgroundResource(R.drawable.basic_transparent_selector);
        //
        //infoButtonContainer.setVisibility(View.GONE);
    }

    private ForegroundColorSpan createTextColorSpan()
    {
        return new ForegroundColorSpan(getResources().getColor(R.color.tradehero_blue));
    }

    //private ClickableSpan createClickableSpan()
    //{
    //    return new ClickableSpan()
    //    {
    //        @Override public void onClick(View view)
    //        {
    //            if (viewDTO != null)
    //            {
    //                //userActionSubject.onNext(new UserAction(viewDTO, UserActionType.RULES));
    //            }
    //        }
    //    };
    //}

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.competition_own_ranking_info)
    public void onInfoButtonClicked()
    {
        if (infoText.getVisibility() == View.GONE)
        {
            infoText.setVisibility(View.VISIBLE);
        }
        else
        {
            infoText.setVisibility(View.GONE);
        }
    }

    public static class CompetitionLeaderboardOwnRankingItemDisplayDto extends CompetitionLeaderboardItemDisplayDto
    {
        @ViewVisibilityValue final int infoButtonContainerVisibility;
        @NonNull final Spanned infoText;

        public CompetitionLeaderboardOwnRankingItemDisplayDto(@NonNull Resources resources,
                @NonNull CurrentUserId currentUserId,
                @NonNull LeaderboardUserDTO leaderboardItem,
                @NonNull UserProfileDTO currentUserProfile,
                int prizeDTOSize,
                @NonNull ProviderDTO providerDTO)
        {
            super(resources, currentUserId, leaderboardItem, currentUserProfile, providerDTO);
            int currentRank = leaderboardItem.ordinalPosition + 1;
            this.infoButtonContainerVisibility = prizeDTOSize != 0 && currentRank > prizeDTOSize ? View.VISIBLE : View.GONE;
            int needed = currentRank - prizeDTOSize;
            this.infoText = THSignedNumber.builder(needed)
                    .format(resources.getString(R.string.leaderboard_ranks_needed))
                    .build().createSpanned();
        }
    }
}
