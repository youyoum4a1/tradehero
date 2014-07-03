package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.thm.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.time.TimeDisplayViewHolder;
import javax.inject.Inject;

public class CompetitionLeaderboardTimedHeader extends LinearLayout
{
    public static final long DEFAULT_UPDATE_MILLISEC_INTERVAL = 200;

    @Inject protected TimeDisplayViewHolder timeDisplayViewHolder;
    protected TextView providerTitle;
    protected TextView ruleDescription;
    protected ProviderDTO providerDTO;
    protected CompetitionDTO competitionDTO;
    protected Runnable viewUpdater;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardTimedHeader(Context context)
    {
        super(context);
    }

    public CompetitionLeaderboardTimedHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionLeaderboardTimedHeader(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        fetchViews();
    }

    protected void fetchViews()
    {
        providerTitle = (TextView) findViewById(R.id.leaderboard_provider_title);
        ruleDescription = (TextView) findViewById(R.id.competition_rule_desc);
        timeDisplayViewHolder.fetchViews(getRootView());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        viewUpdater = new Runnable()
        {
            @Override public void run()
            {
                if (competitionDTO != null && competitionDTO.leaderboard != null)
                {
                    timeDisplayViewHolder.showDuration(competitionDTO.leaderboard.toUtcRestricted);
                }
                postUpdateDurationIfCan();
            }
        };
        postUpdateDurationIfCan();
    }

    @Override protected void onDetachedFromWindow()
    {
        getHandler().removeCallbacks(viewUpdater);
        viewUpdater = null;
        super.onDetachedFromWindow();
    }

    public void postUpdateDurationIfCan()
    {
        postDelayed(viewUpdater, DEFAULT_UPDATE_MILLISEC_INTERVAL);
    }

    public void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        if (andDisplay)
        {
            displayProviderTitle();
            displayRuleDescription();
        }
    }

    public void setCompetitionDTO(CompetitionDTO competitionDTO)
    {
        this.competitionDTO = competitionDTO;
    }

    public void displayProviderTitle()
    {
        if (providerTitle != null)
        {
            if (providerDTO != null
                    && providerDTO.specificResources != null
                    && providerDTO.specificResources.timedHeaderLeaderboardTitleResId > 0)
            {
                providerTitle.setText(providerDTO.specificResources.timedHeaderLeaderboardTitleResId);
            }
            else if (providerDTO != null)
            {
                providerTitle.setText(providerDTO.name);
            }
        }
    }

    public void displayRuleDescription()
    {
        if (ruleDescription != null)
        {
            if (providerDTO != null)
            {
                ruleDescription.setText(providerDTO.ruleText);
            }
        }
    }
}
