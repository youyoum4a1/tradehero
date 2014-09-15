package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th2.R;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.models.number.THSignedMoney;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompetitionLeaderboardMarkUserItemView extends LeaderboardMarkUserItemView
{
    @InjectView(R.id.leaderboard_prize_amount) TextView prizeAmount;
    @Nullable protected ProviderDTO providerDTO;
    @Nullable private PrizeDTO prizeDTO;

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

    public void setProviderDTO(@NotNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        displayLbmuPl();
        if(countryLogo!=null)
        {
            countryLogo.setVisibility(View.GONE);
        }
    }

    public void setPrizeDTO(@Nullable PrizeDTO prizeDTO)
    {
        this.prizeDTO = prizeDTO;
        displayPrize();
    }

    protected void displayPrize()
    {
        if (prizeDTO == null)
        {
            prizeAmount.setVisibility(View.GONE);
        }
        else
        {
            prizeAmount.setVisibility(View.VISIBLE);
            prizeAmount.setText(THSignedMoney.builder(prizeDTO.amount)
                    .currency(prizeDTO.prizeCcy)
                    .build().toString());
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
