package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.models.number.THSignedNumber;
import javax.inject.Inject;

public class CompetitionLeaderboardMarkUserOwnRankingView extends CompetitionLeaderboardMarkUserItemView
{
    @InjectView(R.id.competition_own_ranking_info_container) ViewGroup infoButtonContainer;
    @InjectView(R.id.competition_own_ranking_info_text) TextView infoText;

    @Inject ProviderUtil providerUtil;

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

    @Override protected void displayUserIsNotRanked()
    {
        super.displayUserIsNotRanked();

        String rule = getContext().getString(R.string.leaderboard_see_competition_rules);

        CharacterStyle textColorSpan = createTextColorSpan();
        CharacterStyle clickableSpan = createClickableSpan();

        Spannable span = new SpannableString(rule);
        span.setSpan(clickableSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(textColorSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lbmuRoi.setMovementMethod(LinkMovementMethod.getInstance());
        lbmuRoi.setText(span);
        lbmuRoi.setBackgroundResource(R.drawable.basic_transparent_selector);

        infoButtonContainer.setVisibility(View.GONE);
    }

    @Override public void display(LeaderboardUserDTO leaderboardUserDTO)
    {
        super.display(leaderboardUserDTO);
        displayPrize();
    }

    @Override public void linkWith(@NonNull UserBaseDTO userBaseDTO)
    {
        super.linkWith(userBaseDTO);
    }

    private ForegroundColorSpan createTextColorSpan()
    {
        return new ForegroundColorSpan(getResources().getColor(R.color.tradehero_blue));
    }

    private ClickableSpan createClickableSpan()
    {
        return new ClickableSpan()
        {
            @Override public void onClick(View view)
            {
                handleRulesClicked();
            }
        };
    }

    public void handleRulesClicked()
    {
        Bundle args = new Bundle();
        CompetitionWebViewFragment.putUrl(args, getRules());
        navigator.pushFragment(CompetitionWebViewFragment.class, args);
    }

    public String getRules()
    {
        return providerUtil.getRulesPage(providerDTO.getProviderId());
    }

    @Override protected void handleOpenProfileButtonClicked()
    {
        openTimeline(currentUserId.get());
    }

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

    @Override protected void displayPrize()
    {
        super.displayPrize();
        updateNeededRanks();
    }

    private void updateNeededRanks()
    {
        if (prizeDTOSize != 0 && this.getCurrentRank() != null && this.getCurrentRank() > prizeDTOSize)
        {
            int needed = getCurrentRank() - prizeDTOSize;
            THSignedNumber.builder(needed)
                    .format(getContext().getString(R.string.leaderboard_ranks_needed))
                    .build()
                    .into(infoText);
            infoButtonContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            infoButtonContainer.setVisibility(View.GONE);
        }
    }
}
