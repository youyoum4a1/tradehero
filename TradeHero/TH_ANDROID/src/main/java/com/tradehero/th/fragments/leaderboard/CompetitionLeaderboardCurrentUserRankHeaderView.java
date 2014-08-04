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
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class CompetitionLeaderboardCurrentUserRankHeaderView extends LeaderboardCurrentUserRankHeaderView
{
    private ProviderId providerId;

    @Inject ProviderUtil providerUtil;

    public CompetitionLeaderboardCurrentUserRankHeaderView(Context context)
    {
        super(context);
    }

    public CompetitionLeaderboardCurrentUserRankHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionLeaderboardCurrentUserRankHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void linkWith(@NotNull ProviderId providerId)
    {
        this.providerId = providerId;
    }

    public String getRules()
    {
        return providerUtil.getRulesPage(providerId);
    }

    public void handleRulesClicked()
    {
        if (!isUserRanked())
        {
            Bundle args = new Bundle();
            CompetitionWebViewFragment.putUrl(args, getRules());
            getNavigator().pushFragment(CompetitionWebViewFragment.class, args);
        }
    }

    @Override protected void displayUserNotRanked()
    {
        super.displayUserNotRanked();

        String rule = getContext().getString(R.string.leaderboard_see_competition_rules);

        CharacterStyle clickableSpan = createClickableSpan();
        CharacterStyle textColorSpan = createTextColorSpan();

        Spannable span = new SpannableString(rule);
        span.setSpan(clickableSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(textColorSpan, 0, rule.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mROILabel.setMovementMethod(LinkMovementMethod.getInstance());
        mROILabel.setText(span);
        mROILabel.setBackgroundResource(R.drawable.basic_transparent_selector);
        mDisplayName.setText(R.string.leaderboard_not_ranked);
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

    private ForegroundColorSpan createTextColorSpan()
    {
        return new ForegroundColorSpan(getResources().getColor(R.color.tradehero_blue));
    }
}
