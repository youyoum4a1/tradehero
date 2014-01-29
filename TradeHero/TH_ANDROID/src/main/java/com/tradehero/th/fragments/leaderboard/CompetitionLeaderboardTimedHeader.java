package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.widget.time.TimeDisplayViewHolder;

/**
 * Created by xavier on 1/23/14.
 */
public class CompetitionLeaderboardTimedHeader extends LinearLayout
{
    public static final String TAG = CompetitionLeaderboardTimedHeader.class.getSimpleName();
    public static final long DEFAULT_UPDATE_MILLISEC_INTERVAL = 200;

    protected TextView providerTitle;
    protected TextView ruleDescription;
    protected TimeDisplayViewHolder timeDisplayViewHolder;
    protected ProviderDTO providerDTO;
    protected CompetitionDTO competitionDTO;
    protected ProviderSpecificResourcesDTO providerSpecificResourcesDTO;
    protected Runnable viewUpdater;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardTimedHeader(Context context)
    {
        super(context);
        init();
    }

    public CompetitionLeaderboardTimedHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CompetitionLeaderboardTimedHeader(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    protected void init()
    {
        timeDisplayViewHolder = new TimeDisplayViewHolder(getContext());
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
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
                if (competitionDTO != null)
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

    public void setProviderSpecificResourcesDTO(ProviderSpecificResourcesDTO providerSpecificResourcesDTO)
    {
        this.providerSpecificResourcesDTO = providerSpecificResourcesDTO;
    }

    public void displayProviderTitle()
    {
        if (providerTitle != null)
        {
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.timedHeaderLeaderboardTitleResId > 0)
            {
                providerTitle.setText(providerSpecificResourcesDTO.timedHeaderLeaderboardTitleResId);
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
