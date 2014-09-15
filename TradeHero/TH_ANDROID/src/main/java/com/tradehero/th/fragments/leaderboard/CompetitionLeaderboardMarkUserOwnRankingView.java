package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th2.R;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class CompetitionLeaderboardMarkUserOwnRankingView extends CompetitionLeaderboardMarkUserItemView
{
    @Inject ProviderUtil providerUtil;

    public CompetitionLeaderboardMarkUserOwnRankingView(Context context)
    {
        super(context);
    }

    public CompetitionLeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionLeaderboardMarkUserOwnRankingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void displayUserIsNotRanked()
    {
        super.displayUserIsNotRanked();
        lbmuDisplayName.setText(R.string.leaderboard_not_ranked);

        String rule = getContext().getString(R.string.leaderboard_see_competition_rules);

        CharacterStyle textColorSpan = createTextColorSpan();
        CharacterStyle clickableSpan = createClickableSpan();

        Spannable span = new SpannableString(rule);
        span.setSpan(clickableSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(textColorSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        lbmuRoi.setMovementMethod(LinkMovementMethod.getInstance());
        lbmuRoi.setText(span);
        lbmuRoi.setBackgroundResource(R.drawable.basic_transparent_selector);
    }

    @Override public void linkWith(@NotNull UserBaseDTO userBaseDTO)
    {
        super.linkWith(userBaseDTO);
        if (getCurrentRank() == null)
        {
            lbmuDisplayName.setText(R.string.leaderboard_not_ranked);
        }
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
        getNavigator().pushFragment(CompetitionWebViewFragment.class, args);
    }

    public String getRules()
    {
        return providerUtil.getRulesPage(providerDTO.getProviderId());
    }

    @Override protected void handleOpenProfileButtonClicked()
    {
        openTimeline(currentUserId.get());
    }
}
