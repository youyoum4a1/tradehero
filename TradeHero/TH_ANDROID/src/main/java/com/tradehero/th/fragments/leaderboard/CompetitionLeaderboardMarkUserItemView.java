package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;

public class CompetitionLeaderboardMarkUserItemView extends LeaderboardMarkUserItemView
{
    private ProviderDTO providerDTO;
    private PrizeDTO prizeDTO;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardMarkUserItemView(Context context)
    {
        super(context);
    }

    public CompetitionLeaderboardMarkUserItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionLeaderboardMarkUserItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public void setProviderDTO(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        displayLbmuPl();
    }

    public void setPrizeDTO(PrizeDTO prizeDTO)
    {
        this.prizeDTO = prizeDTO;
        displayPrize();
    }

    protected void displayPrize()
    {
        if(prizeDTO==null)
        {
            prizelayout.setVisibility(View.GONE);
        }
        else
        {
            prizelayout.setVisibility(View.VISIBLE);
            prizeAmount.setText(prizeDTO.prizeCcy+(int)prizeDTO.amount);
        }
    }

    @Override protected String getLbmuPlCurrencyDisplay()
    {
        if (leaderboardItem != null && leaderboardItem.hasValidCurrencyDisplay())
        {
            return leaderboardItem.getNiceCurrency();
        }
        else if (providerDTO != null && providerDTO.hasValidCurrencyDisplay())
        {
            return providerDTO.getNiceCurrency();
        }
        return super.getLbmuPlCurrencyDisplay();
    }

    @Override protected void pushLeaderboardPositionListFragment(GetPositionsDTOKey getPositionsDTOKey, LeaderboardDefDTO leaderboardDefDTO)
    {
        // leaderboard mark user id, to get marking user information
        Bundle bundle = new Bundle();

        CompetitionLeaderboardPositionListFragment.putGetPositionsDTOKey(bundle, leaderboardItem.getLeaderboardMarkUserId());
        CompetitionLeaderboardPositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());
        CompetitionLeaderboardPositionListFragment.putProviderId(bundle, providerDTO.getProviderId());

        if (applicablePortfolioId != null)
        {
            CompetitionLeaderboardPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        getNavigator().pushFragment(CompetitionLeaderboardPositionListFragment.class, bundle);
    }
}
