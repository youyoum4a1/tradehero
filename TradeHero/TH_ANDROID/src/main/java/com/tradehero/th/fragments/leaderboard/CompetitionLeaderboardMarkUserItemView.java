package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;

public class CompetitionLeaderboardMarkUserItemView extends LeaderboardMarkUserItemView
{
    private ProviderDTO providerDTO;

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
        getNavigator().pushFragment(CompetitionLeaderboardPositionListFragment.class, bundle);

    }
}
